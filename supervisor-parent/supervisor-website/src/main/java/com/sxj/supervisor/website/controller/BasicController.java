package com.sxj.supervisor.website.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.bouncycastle.openssl.PEMReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import sun.security.x509.X500Name;
import third.rewrite.fastdfs.NameValuePair;
import third.rewrite.fastdfs.service.IStorageClientService;

import com.sxj.redis.core.pubsub.RedisTopics;
import com.sxj.spring.modules.mapper.JsonMapper;
import com.sxj.supervisor.entity.contract.ContractItemEntity;
import com.sxj.supervisor.entity.member.AccountEntity;
import com.sxj.supervisor.entity.member.MemberEntity;
import com.sxj.supervisor.entity.member.MemberFunctionEntity;
import com.sxj.supervisor.entity.member.MemberLogEntity;
import com.sxj.supervisor.entity.system.AreaEntity;
import com.sxj.supervisor.enu.member.AccountStatesEnum;
import com.sxj.supervisor.enu.member.MemberCheckStateEnum;
import com.sxj.supervisor.enu.member.MemberStatesEnum;
import com.sxj.supervisor.enu.member.MemberTypeEnum;
import com.sxj.supervisor.model.comet.MessageChannel;
import com.sxj.supervisor.model.comet.RfidChannel;
import com.sxj.supervisor.model.contract.ContractModel;
import com.sxj.supervisor.model.contract.ContractQuery;
import com.sxj.supervisor.model.login.SupervisorPrincipal;
import com.sxj.supervisor.model.member.MemberFunctionModel;
import com.sxj.supervisor.model.member.MemberQuery;
import com.sxj.supervisor.service.contract.IContractService;
import com.sxj.supervisor.service.member.IAccountService;
import com.sxj.supervisor.service.member.IMemberFunctionService;
import com.sxj.supervisor.service.member.IMemberLogService;
import com.sxj.supervisor.service.member.IMemberRoleService;
import com.sxj.supervisor.service.member.IMemberService;
import com.sxj.supervisor.service.system.IAreaService;
import com.sxj.supervisor.website.login.SupervisorShiroRedisCache;
import com.sxj.supervisor.website.login.SupervisorSiteToken;
import com.sxj.util.comet.CometServiceImpl;
import com.sxj.util.common.FileUtil;
import com.sxj.util.common.StringUtils;
import com.sxj.util.exception.WebException;
import com.sxj.util.logger.SxjLogger;

@Controller
public class BasicController extends BaseController
{
    
    @Autowired
    private IMemberFunctionService functionService;
    
    @Autowired
    private IMemberService memberService;
    
    @Autowired
    private IAccountService accountService;
    
    @Autowired
    private IMemberRoleService roleService;
    
    @Autowired
    private IMemberLogService logService;
    
    @Autowired
    private IAreaService areaService;
    
    @Autowired
    private IStorageClientService storageClientService;
    
    @Autowired
    private RedisTopics topics;
    
    /**
     * getRecordState 合同service
     */
    @Autowired
    private IContractService contractService;
    
    //    @PostConstruct
    //    public void init()
    //    {
    //        CometEngine engine = CometContext.getInstance().getEngine();
    //        // 启动 Comet Server Thread
    //        MessageThread cometServer = MessageThread.newInstance(engine);
    //        RTopic<String> topic1 = topics.getTopic("topic1");
    //        topic1.addListener(new CometMessageListener(cometServer));
    //    }
    
    @RequestMapping("notifyComet")
    public @ResponseBody void notifyComet(String channelName)
    {
        CometServiceImpl.sendMessage("topic1", channelName);
    }
    
    @RequestMapping("index")
    public String ToIndex(HttpServletRequest request, ModelMap map)
    {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userinfo") == null)
        {
            return LOGIN;
        }
        else
        {
            SupervisorPrincipal info = getLoginInfo(session);
            if (info.getAccount() != null && info.getMember() != null)
            {
                AccountEntity newAccount = accountService.getAccount(info.getAccount()
                        .getId());
                if (newAccount == null)
                {
                    return LOGIN;
                }
                if (newAccount.getState().equals(AccountStatesEnum.STOP))
                {
                    return LOGIN;
                }
                if (StringUtils.isEmpty(newAccount.getPassword()))
                {
                    return LOGIN;
                }
                if (!newAccount.getPassword().equals(info.getAccount()
                        .getPassword()))
                {
                    return LOGIN;
                }
                return "site/member/account-index";
            }
            else if (info.getAccount() == null && info.getMember() != null)
            {
                List<AreaEntity> cityList = areaService.getChildrenAreas("32");
                MemberEntity member = memberService.getMember(info.getMember()
                        .getId());
                if (member.getAccountNum() == null)
                {
                    member.setAccountNum(0);
                }
                map.put("cityList", cityList);
                map.put("member", member);
                if (info.getMember().getFlag())
                {
                    
                    Long systemMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_SYSTEM_MESSAGE_COUNT
                            + member.getMemberNo());
                    Long transMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_TRANS_MESSAGE_COUNT
                            + member.getMemberNo());
                    Long tenderMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_TENDER_MESSAGE_COUNT
                            + member.getMemberNo());
                    map.put("systemMessageCount", systemMessageCount);
                    map.put("transMessageCount", transMessageCount);
                    map.put("tenderMessageCount", tenderMessageCount);
                    
                    map.put("channelName_sys",
                            MessageChannel.MEMBER_SYSTEM_MESSAGE_COUNT
                                    + member.getMemberNo());
                    map.put("channelName_trans",
                            MessageChannel.MEMBER_TRANS_MESSAGE_COUNT
                                    + member.getMemberNo());
                    map.put("channelName_tender",
                            MessageChannel.MEMBER_TENDER_MESSAGE_COUNT
                                    + member.getMemberNo());
                    return "site/member/member-profile";
                }
                else
                {
                    return "site/member/edit-member";
                }
            }
            else
            {
                return LOGIN;
            }
            
        }
        
    }
    
    @RequestMapping("to_login")
    public String ToLogin(HttpServletRequest request, ModelMap map)
            throws WebException
    {
        try
        {
            //X509Certificate[] clientCertChain = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
            String certString = request.getHeader("client-cert");
            if (StringUtils.isEmpty(certString))
            {
                return LOGINPAGER;
            }
            certString = certString.replaceAll("\t", "\n");
            X509Certificate clientCertChain = (X509Certificate) new PEMReader(
                    new StringReader(certString), null, "SUN").readObject();
            if (clientCertChain == null)
            {
                return LOGINPAGER;
            }
            else
            {
                Principal dn = clientCertChain.getSubjectDN();
                X500Name x509Principal = (X500Name) dn;
                String uid = x509Principal.getGivenName();
                if (StringUtils.isNotEmpty(uid))
                {
                    String[] uids = uid.split(",");
                    map.put("accountName", uids[1]);
                    map.put("memberName", uids[0]);
                }
                
            }
            
            return LOGINPAGER;
        }
        catch (Exception e)
        {
            throw new WebException("系统错误", e);
        }
    }
    
    @RequestMapping("logout")
    public String logout(HttpServletRequest request)
    {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return "redirect:" + getBasePath(request) + "to_login.htm";
        
    }
    
    @RequestMapping("error")
    public String ToError()
    {
        return "site/500";
    }
    
    @RequestMapping("404")
    public String To404()
    {
        return "site/404";
    }
    
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(String memberName, String accountName, String password,
            HttpSession session, HttpServletRequest request, ModelMap map)
    {
        map.put("accountName", accountName);
        map.put("memberName", memberName);
        SupervisorSiteToken token = null;
        SupervisorPrincipal userBean = null;
        AccountEntity account = null;
        if (StringUtils.isNotEmpty(memberName)
                && StringUtils.isNotEmpty(accountName))
        {
            MemberEntity member = memberService.getMemberByName(memberName);
            if (member == null)
            {
                map.put("message", "会员不存在");
                return LOGIN;
            }
            if (!member.getName().equals(memberName))
            {
                map.put("message", "会员名错误");
                return LOGIN;
            }
            if (MemberCheckStateEnum.UNAUDITED.equals(member.getCheckState()))
            {
                map.put("message", "会员未审核");
                return LOGIN;
            }
            if (MemberStatesEnum.STOP.equals(member.getState()))
            {
                map.put("message", "会员已冻结");
                return LOGIN;
            }
            
            account = accountService.getAccountByName(accountName,
                    member.getMemberNo());
            if (account == null)
            {
                map.put("amessage", "会员子账户不存在");
                return LOGIN;
            }
            if (AccountStatesEnum.STOP.equals(account.getState()))
            {
                map.put("amessage", "会员子账户已冻结");
                return LOGIN;
            }
            
            userBean = new SupervisorPrincipal();
            userBean.setAccount(account);
            userBean.setMember(member);
            token = new SupervisorSiteToken(userBean, password);
        }
        else if (StringUtils.isNotEmpty(memberName)
                && StringUtils.isEmpty(accountName))
        {
            MemberEntity member = memberService.getMemberByName(memberName);
            if (member == null)
            {
                map.put("message", "会员不存在");
                return LOGIN;
            }
            if (MemberCheckStateEnum.UNAUDITED.equals(member.getCheckState()))
            {
                map.put("message", "会员未审核");
                return LOGIN;
            }
            if (MemberStatesEnum.STOP.equals(member.getState()))
            {
                map.put("message", "会员已冻结");
                return LOGIN;
            }
            userBean = new SupervisorPrincipal();
            userBean.setMember(member);
            token = new SupervisorSiteToken(userBean, password);
        }
        else
        {
            map.put("message", "公司名称和密码不能为空");
            //map.put("pmessage", "密码不能为空");
            return LOGIN;
        }
        Subject currentUser = SecurityUtils.getSubject();
        try
        {
            currentUser.login(token);
            PrincipalCollection principals = currentUser.getPrincipals();
            if (userBean.getAccount() != null)
            {
                SupervisorShiroRedisCache.addToMap(userBean.getAccount()
                        .getId(), principals);
            }
            else
            {
                SupervisorShiroRedisCache.addToMap(userBean.getMember()
                        .getMemberNo(), principals);
            }
        }
        catch (AuthenticationException e)
        {
            SxjLogger.error("登陆失败", e, this.getClass());
            map.put("pmessage", "密码错误");
            return LOGIN;
            
        }
        if (currentUser.isAuthenticated())
        {
            session.setAttribute("userinfo", userBean);
            if (account != null)
            {
                accountService.edit_Login(account.getId());
            }
            return "redirect:" + getBasePath(request) + "index.htm";
        }
        else
        {
            map.put("message", "登陆失败");
            return LOGIN;
        }
    }
    
    /**
     * 左侧菜单
     * 
     * @param map
     * @return
     */
    @RequestMapping("menu")
    public String ToMenu(HttpServletRequest request, ModelMap map)
    {
        HttpSession session = request.getSession(false);
        if (session.getAttribute("userinfo") == null)
        {
            return LOGIN;
        }
        SupervisorPrincipal userBean = (SupervisorPrincipal) session.getAttribute("userinfo");
        if (userBean.getMember() != null && userBean.getAccount() == null)
        {
            MemberTypeEnum type = userBean.getMember().getType();
            int flag = 0;
            if (MemberTypeEnum.DAWP.equals(type))
            {
                flag = 1;
            }
            else
            {
                flag = 2;
            }
            List<MemberFunctionModel> list = functionService.queryFunctions(flag);
            map.put("list", list);
        }
        else if (userBean.getMember() != null && userBean.getAccount() != null)
        {
            List<MemberFunctionModel> list = roleService.getRoleFunctions(userBean.getAccount()
                    .getId());
            map.put("list", list);
        }
        if (userBean.getMember() != null)
        {
            Long transMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_TRANS_MESSAGE_COUNT
                    + userBean.getMember().getMemberNo());
            Long sysMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_SYSTEM_MESSAGE_COUNT
                    + userBean.getMember().getMemberNo());
            Long tenderMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_TENDER_MESSAGE_COUNT
                    + userBean.getMember().getMemberNo());
            Long contractMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_CONTRACT_MESSAGE_COUNT
                    + userBean.getMember().getMemberNo());
            Long payMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_PAY_MESSAGE_COUNT
                    + userBean.getMember().getMemberNo());
            if (tenderMessageCount <= 0)
            {
                String key = MessageChannel.MEMBER_TENDER_MESSAGE_COUNT
                        + userBean.getMember().getMemberNo();
                List<String> userKeys = CometServiceImpl.get(MessageChannel.MEMBER_READTENDER_MESSAGE_KEYS);
                Long totalCount = CometServiceImpl.getCount(MessageChannel.MEMBER_TENDER_MESSAGE_COUNT);
                if (userKeys != null && userKeys.size() > 0)
                {
                    if (!userKeys.contains(key))
                    {
                        CometServiceImpl.setCount(key, totalCount);
                        tenderMessageCount = totalCount;
                    }
                }
                else
                {
                    CometServiceImpl.setCount(key, totalCount);
                    tenderMessageCount = totalCount;
                }
            }
            map.put("transMessageCount", transMessageCount);
            map.put("sysMessageCount", sysMessageCount);
            map.put("tenderMessageCount", tenderMessageCount);
            map.put("contractMessageCount", contractMessageCount);
            map.put("payMessageCount", payMessageCount);
        }
        return "site/menu";
    }
    
    /**
     * 上传文件
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("upload")
    public void uploadFile(HttpServletRequest request,
            HttpServletResponse response) throws IOException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        if (!(request instanceof DefaultMultipartHttpServletRequest))
        {
            return;
        }
        DefaultMultipartHttpServletRequest re = (DefaultMultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMaps = re.getFileMap();
        Collection<MultipartFile> files = fileMaps.values();
        List<String> fileIds = new ArrayList<String>();
        for (MultipartFile myfile : files)
        {
            if (myfile.isEmpty())
            {
                System.err.println("文件未上传");
            }
            else
            {
                String originalName = myfile.getOriginalFilename();
                String extName = FileUtil.getFileExtName(originalName);
                String filePath = storageClientService.uploadFile(null,
                        new ByteArrayInputStream(myfile.getBytes()),
                        myfile.getBytes().length,
                        extName.toUpperCase());
                SxjLogger.info("siteUploadFilePath=" + filePath,
                        this.getClass());
                fileIds.add(filePath);
                
                // 上传元数据
                NameValuePair[] metaList = new NameValuePair[1];
                metaList[0] = new NameValuePair("originalName", originalName);
                storageClientService.overwriteMetadata(filePath, metaList);
            }
        }
        map.put("fileIds", fileIds);
        String res = JsonMapper.nonDefaultMapper().toJson(map);
        response.setContentType("text/plain;UTF-8");
        PrintWriter out = response.getWriter();
        out.print(res);
        out.flush();
        out.close();
    }
    
    /**
     * 删除文件
     * @param fileId
     */
    @RequestMapping("deleteFile")
    public @ResponseBody Map<String, String> deleteFile(String fileId)
            throws IOException
    {
        Map<String, String> map = new HashMap<String, String>();
        try
        {
            storageClientService.deleteFile(fileId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            SxjLogger.debug("删除失败!", e.getClass());
        }
        return map;
    }
    
    /**
     * 下载文件
     * @param request
     * @param response
     * @param filePath
     * @throws IOException
     */
    @RequestMapping("downloadFile")
    public void downloadFile(HttpServletRequest request,
            HttpServletResponse response, String filePath) throws IOException
    {
        try
        {
            ServletOutputStream output = response.getOutputStream();
            String fileName = "扫描件" + stringDate();
            String group = filePath.substring(0, filePath.indexOf("/"));
            response.addHeader("Content-Disposition", "attachment;filename="
                    + fileName + ".pdf");
            response.setContentType("application/pdf");
            String path = filePath.substring(filePath.indexOf("/") + 1,
                    filePath.length());
            storageClientService.downloadFile(group, path, output);
            output.flush();
            output.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            SxjLogger.debug("下载文件出错", e.getClass());
        }
    }
    
    private String stringDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//获取年份         
        int month = cal.get(Calendar.MONTH);//获取月份         
        int day = cal.get(Calendar.DATE);//获取日         
        int hour = cal.get(Calendar.HOUR);//小时          
        int minute = cal.get(Calendar.MINUTE);//分                     
        int second = cal.get(Calendar.SECOND);//秒     
        String date = year + "" + month + "" + day + "" + hour + "" + minute
                + "" + second;
        
        return date;
    }
    
    /**
     * 甲方联想
     * 
     * @param request
     * @param response
     * @param keyword
     * @return
     * @throws IOException
     */
    @RequestMapping("autoCompleA")
    public @ResponseBody Map<String, String> autoCompleA(
            HttpServletRequest request, HttpServletResponse response,
            String keyword) throws IOException
    {
        MemberQuery mq = new MemberQuery();
        if (keyword != "" && keyword != null)
        {
            mq.setMemberName(keyword);
        }
        mq.setMemberType(0);
        List<MemberEntity> list = memberService.queryMembers(mq);
        List strlist = new ArrayList();
        String sb = "";
        for (MemberEntity memberEntity : list)
        {
            sb = "{\"title\":\"" + memberEntity.getName() + "\",\"result\":\""
                    + memberEntity.getMemberNo() + "\"}";
            strlist.add(sb);
        }
        String json = "{\"data\":" + strlist.toString() + "}";
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
        out.close();
        return null;
    }
    
    /**
     * 获取招标合同号
     * 
     * @param request
     * @param response
     * @param keyword
     * @return
     * @throws IOException
     */
    @RequestMapping("refContractNo")
    public @ResponseBody Map<String, String> getRefContractNo(
            HttpSession session, HttpServletRequest request,
            HttpServletResponse response, String keyword) throws IOException
    {
        ContractQuery query = new ContractQuery();
        if (keyword != "" && keyword != null)
        {
            query.setEngAddress(keyword);
        }
        SupervisorPrincipal userBean = (SupervisorPrincipal) session.getAttribute("userinfo");
        query.setMemberIdB(userBean.getMember().getMemberNo());
        List<ContractModel> list = contractService.queryContracts(query);
        List strlist = new ArrayList();
        String sb = "";
        for (ContractModel cm : list)
        {
            if (cm != null)
            {
                sb = "{\"title\":\"" + cm.getContract().getEngAddress()
                        + "\",\"result\":\"" + cm.getContract().getContractNo()
                        + "\"}";
                strlist.add(sb);
            }
            
        }
        String json = "{\"data\":" + strlist.toString() + "}";
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
        out.close();
        return null;
    }
    
    /**
     * 乙方联想
     * 
     * @param request
     * @param response
     * @param keyword
     * @return
     * @throws IOException
     */
    @RequestMapping("autoCompleB")
    public @ResponseBody Map<String, String> autoCompleB(
            HttpServletRequest request, HttpServletResponse response,
            String keyword) throws IOException
    {
        MemberQuery mq = new MemberQuery();
        if (keyword != "" && keyword != null)
        {
            mq.setMemberName(keyword);
        }
        mq.setMemberTypeB(0);
        List<MemberEntity> list = memberService.queryMembers(mq);
        List strlist = new ArrayList();
        String sb = "";
        for (MemberEntity memberEntity : list)
        {
            sb = "{\"title\":\"" + memberEntity.getName() + "\",\"result\":\""
                    + memberEntity.getMemberNo() + "\"}";
            strlist.add(sb);
        }
        String json = "{\"data\":" + strlist.toString() + "}";
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
        out.close();
        return null;
    }
    
    /**
     * 会员联想
     * 
     * @param request
     * @param response
     * @param keyword
     * @return
     * @throws IOException
     */
    @RequestMapping("autoCompleMember")
    public @ResponseBody Map<String, String> autoCompleMember(
            HttpServletRequest request, HttpServletResponse response,
            String keyword) throws IOException
    {
        MemberQuery mq = new MemberQuery();
        if (keyword != "" && keyword != null)
        {
            mq.setMemberName(keyword);
        }
        //mq.setMemberTypeB(0);
        List<MemberEntity> list = memberService.queryMembers(mq);
        List strlist = new ArrayList();
        String sb = "";
        for (MemberEntity memberEntity : list)
        {
            sb = "{\"title\":\"" + memberEntity.getName() + "\",\"result\":\""
                    + memberEntity.getMemberNo() + "\"}";
            strlist.add(sb);
        }
        String json = "{\"data\":" + strlist.toString() + "}";
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
        out.close();
        return null;
    }
    
    @RequestMapping("filesort")
    public @ResponseBody List<String> fileSort(String fileId)
            throws IOException
    {
        List<String> sortFile = new ArrayList<String>();
        try
        {
            if (StringUtils.isEmpty(fileId))
            {
                return sortFile;
            }
            String[] fileids = fileId.split(",");
            Map<String, String> nameMap = new TreeMap<String, String>();
            Map<String, NameValuePair[]> values = storageClientService.getMetadata(fileids);
            for (String key : values.keySet())
            {
                if (key == null)
                {
                    continue;
                }
                NameValuePair[] value = values.get(key);
                nameMap.put(key, value[0].getValue());
            }
            List<Map.Entry<String, String>> mappingList = null;
            // 通过ArrayList构造函数把map.entrySet()转换成list
            mappingList = new ArrayList<Map.Entry<String, String>>(
                    nameMap.entrySet());
            // 通过比较器实现比较排序
            Collections.sort(mappingList,
                    new Comparator<Map.Entry<String, String>>()
                    {
                        public int compare(Map.Entry<String, String> mapping1,
                                Map.Entry<String, String> mapping2)
                        {
                            return mapping1.getValue()
                                    .compareTo(mapping2.getValue());
                        }
                    });
            for (Map.Entry<String, String> mapping : mappingList)
            {
                sortFile.add(mapping.getKey());
            }
            // Map<String, Object> map = new HashMap<String, Object>();
            // map.put("", sortFile);
        }
        catch (Exception e)
        {
            SxjLogger.error(e.getMessage(), e, this.getClass());
        }
        return sortFile;
        
    }
    
    public static String getIpAddr(HttpServletRequest request)
    {
        String strUserIp = null;
        /** * */
        // Apache 代理 解决IP地址问题
        strUserIp = request.getHeader("X-Forwarded-For");
        if (strUserIp == null || strUserIp.length() == 0
                || "unknown".equalsIgnoreCase(strUserIp))
        {
            strUserIp = request.getHeader("Proxy-Client-IP");
        }
        if (strUserIp == null || strUserIp.length() == 0
                || "unknown".equalsIgnoreCase(strUserIp))
        {
            strUserIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (strUserIp == null || strUserIp.length() == 0
                || "unknown".equalsIgnoreCase(strUserIp))
        {
            strUserIp = request.getRemoteAddr();
        }
        // 解决获取多网卡的IP地址问题
        if (strUserIp != null)
        {
            strUserIp = strUserIp.split(",")[0];
        }
        else
        {
            strUserIp = "127.0.0.1";
        }
        // 解决获取IPv6地址 暂时改为本机地址模式
        if (strUserIp.length() > 16)
        {
            strUserIp = "127.0.0.1";
        }
        return strUserIp;
        // 没有IP Apache 代理 解决IP地址问题
        // strUserIp=request.getRemoteAddr();
        // if (strUserIp != null) {strUserIp = strUserIp.split(",")[0];}
        // return strUserIp;
    }
    
    @RequestMapping("enter")
    @ResponseBody
    public void enter(String functionId, String url, HttpSession session,
            HttpServletRequest request)
    {
        Date enterTime = (Date) session.getAttribute("enterTime");
        Date nowTime = new Date();
        String currentUrl = (String) session.getAttribute("currentUrl");
        String currentFunction = (String) session.getAttribute("currentFunction");
        String nextUrl = (String) session.getAttribute("nextUrl");
        String nextFunction = (String) session.getAttribute("nextFunction");
        if (currentUrl == null)
        {
            session.setAttribute("currentUrl", request.getHeader("Referer"));
            
        }
        session.setAttribute("previousUrl", currentUrl);
        session.setAttribute("currentUrl", nextUrl);
        session.setAttribute("nextUrl", url);
        
        session.setAttribute("previousFunction", currentFunction);
        session.setAttribute("currentFunction", nextFunction);
        session.setAttribute("nextFunction", functionId);
        
        MemberEntity principal = (MemberEntity) SecurityUtils.getSubject()
                .getPrincipal();
        if (principal != null)
        {
            MemberLogEntity log = new MemberLogEntity();
            log.setMemberNo(principal.getMemberNo());
            log.setMemberName(principal.getName());
            log.setNowPage(nextUrl);
            log.setNextpage(url);
            log.setPrePage(currentUrl);
            
            MemberFunctionEntity function = functionService.getFunction(nextFunction);
            if (function != null)
            {
                log.setNowName(function.getTitle());
            }
            
            function = functionService.getFunction(functionId);
            if (function != null)
            {
                log.setNextName(function.getTitle());
            }
            
            function = functionService.getFunction(currentFunction);
            if (function != null)
            {
                log.setPreName(function.getTitle());
            }
            
            log.setCallTime(enterTime);
            log.setIp(getIpAddr(request));
            if (enterTime != null)
            {
                long waitTime = (nowTime.getTime() - enterTime.getTime()) / 1000;
                log.setWaitTime(waitTime + "");
            }
            logService.addLog(log);
        }
        session.setAttribute("enterTime", nowTime);
        
    }
    
    @RequestMapping("message")
    public @ResponseBody Map<String, Object> message(
            HttpServletRequest request, HttpServletResponse response,
            String channelName) throws IOException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        if (channelName.contains(MessageChannel.RECORD_MESSAGE)
                || channelName.contains(RfidChannel.RFID_APPLY_MESSAGE)
                || channelName.contains(MessageChannel.WEBSITE_PAY_MESSAGE)
                || channelName.contains(MessageChannel.WEBSITE_FINANCE_MESSAGE)
                || channelName.contains(MessageChannel.MEMBER_MESSAGE)
                || channelName.contains(MessageChannel.MEMBER_PERFECT_MESSAGE)
                || channelName.contains(MessageChannel.MEMBER_SYSTEM_MESSAGE_COUNT)
                || channelName.contains(MessageChannel.MEMBER_TRANS_MESSAGE_COUNT)
                || channelName.contains(MessageChannel.MEMBER_TENDER_MESSAGE_COUNT)
                || channelName.contains(MessageChannel.MEMBER_CONTRACT_MESSAGE_COUNT)
                || channelName.contains(MessageChannel.MEMBER_PAY_MESSAGE_COUNT))
        {
            Long count = CometServiceImpl.getCount(channelName);
            SxjLogger.debug("Sending Message to Comet Client:" + count,
                    getClass());
            //if (count > 0)
            //  {
            map.put("count", count);
            map.put("channelName", channelName);
            return map;
            // }
        }
        else
        {
            List<String> cache = CometServiceImpl.get(channelName);
            SxjLogger.debug("Sending Message to Comet Client:" + cache,
                    getClass());
            if (cache != null && cache.size() > 0)
            {
                map.put("cache", cache);
                map.put("channelName", channelName);
                return map;
            }
        }
        return null;
    }
    
    @RequestMapping("menuMessageCount")
    public @ResponseBody Map<String, Long> menuMessageCount(HttpSession session)
            throws WebException
    {
        Map<String, Long> map = new HashMap<>();
        map.put("isNotLogin", 0l);
        try
        {
            SupervisorPrincipal login = getLoginInfo(session);
            if (login == null)
            {
                map.put("isNotLogin", 1l);
                return map;
            }
            MemberEntity member = login.getMember();
            Long transMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_TRANS_MESSAGE_COUNT
                    + member.getMemberNo());
            Long sysMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_SYSTEM_MESSAGE_COUNT
                    + member.getMemberNo());
            Long tenderMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_TENDER_MESSAGE_COUNT
                    + member.getMemberNo());
            if (tenderMessageCount <= 0)
            {
                String key = MessageChannel.MEMBER_TENDER_MESSAGE_COUNT
                        + login.getMember().getMemberNo();
                List<String> userKeys = CometServiceImpl.get(MessageChannel.MEMBER_READTENDER_MESSAGE_KEYS);
                Long totalCount = CometServiceImpl.getCount(MessageChannel.MEMBER_TENDER_MESSAGE_COUNT);
                if (userKeys != null && userKeys.size() > 0)
                {
                    if (!userKeys.contains(key))
                    {
                        CometServiceImpl.setCount(key, totalCount);
                        tenderMessageCount = totalCount;
                    }
                }
                else
                {
                    CometServiceImpl.setCount(key, totalCount);
                    tenderMessageCount = totalCount;
                }
            }
            Long contractMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_CONTRACT_MESSAGE_COUNT
                    + member.getMemberNo());
            Long payMessageCount = CometServiceImpl.getCount(MessageChannel.MEMBER_PAY_MESSAGE_COUNT
                    + member.getMemberNo());
            map.put("transMessageCount", transMessageCount);
            map.put("sysMessageCount", sysMessageCount);
            map.put("tenderMessageCount", tenderMessageCount);
            map.put("contractMessageCount", contractMessageCount);
            map.put("payMessageCount", payMessageCount);
        }
        catch (Exception e)
        {
            SxjLogger.error(e.getMessage(), e, this.getClass());
        }
        return map;
        
    }
    
    /**
     * 获取招标门窗型号
     * 
     * @param request
     * @param response
     * @param keyword
     * @return
     * @throws IOException
     */
    @RequestMapping("grtWindowType")
    public @ResponseBody Map<String, String> getRefContractWindwoType(
            HttpSession session, HttpServletRequest request,
            HttpServletResponse response, String keyword, String refContractNo)
            throws IOException
    {
        if (StringUtils.isEmpty(refContractNo))
        {
            return null;
        }
        List<ContractItemEntity> list = contractService.getRefContractItem(refContractNo,
                keyword);
        List strlist = new ArrayList();
        String sb = "";
        for (ContractItemEntity ci : list)
        {
            if (ci != null)
            {
                sb = "{\"title\":\"" + ci.getWindowType() + "\",\"result\":\""
                        + ci.getId() + "\"}";
                strlist.add(sb);
            }
            
        }
        String json = "{\"data\":" + strlist.toString() + "}";
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
        out.close();
        return null;
    }
}
