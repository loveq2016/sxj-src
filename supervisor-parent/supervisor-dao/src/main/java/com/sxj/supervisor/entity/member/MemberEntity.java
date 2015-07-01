//
//
//  Generated by StarUML(tm) Java Add-In

//
//  @ Project : 私享家架构设计
//  @ File Name : MemberEntity.java
//  @ Date : 2014/8/11
//  @ Author :   
//
//

package com.sxj.supervisor.entity.member;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.sxj.mybatis.orm.annotations.Column;
import com.sxj.mybatis.orm.annotations.Entity;
import com.sxj.mybatis.orm.annotations.GeneratedValue;
import com.sxj.mybatis.orm.annotations.GenerationType;
import com.sxj.mybatis.orm.annotations.Id;
import com.sxj.mybatis.orm.annotations.Sn;
import com.sxj.mybatis.orm.annotations.Table;
import com.sxj.mybatis.orm.annotations.Version;
import com.sxj.mybatis.pagination.Pagable;
import com.sxj.supervisor.dao.member.IMemberDao;
import com.sxj.supervisor.enu.member.LevelEnum;
import com.sxj.supervisor.enu.member.MemberCheckStateEnum;
import com.sxj.supervisor.enu.member.MemberStatesEnum;
import com.sxj.supervisor.enu.member.MemberTypeEnum;
import com.sxj.supervisor.validator.hibernate.AddGroup;
import com.sxj.supervisor.validator.hibernate.UpdateGroup;

/**
 * 会员信息实体
 * 
 * @author Administrator
 *
 */
@Entity(mapper = IMemberDao.class)
@Table(name = "M_MEMBER")
public class MemberEntity extends Pagable implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 8851524763229466282L;
    
    /**
     * 标识
     **/
    @Id(column = "ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    /**
     * 会员号
     **/
    @Column(name = "MEMBER_NO")
    @Sn(pattern = "000000", step = 1, table = "T_SN", stub = "F_SN_NAME", sn = "F_SN_NUMBER", stubValueProperty = "noType")
    private String memberNo;
    
    private String noType;
    
    /**
     * 会员密码
     **/
    @Column(name = "PASSWORD")
    @NotEmpty(message = "会员密码不能为空", groups = { AddGroup.class })
    @Length(max = 50, message = "会员密码长度过长", groups = { AddGroup.class })
    private String password;
    
    /**
     * 会员名称
     **/
    @Column(name = "NAME")
    @NotEmpty(message = "会员名称不能为空", groups = { AddGroup.class })
    @Length(max = 50, message = "会员名称长度过长")
    private String name;
    
    /**
     * 营业执照号
     **/
    @Column(name = "B_LICENSE_NO")
    @Length(max = 15, message = "营业执照号长度过长", groups = { AddGroup.class,
            UpdateGroup.class })
    private String bLicenseNo;
    
    /**
     * 会员类型
     **/
    @Column(name = "TYPE")
    @NotNull(message = "会员类型不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private MemberTypeEnum type;
    
    /**
     * 地理区域
     **/
    @Column(name = "AREA")
    @NotEmpty(message = "地理区域不能为空", groups = { AddGroup.class,
            UpdateGroup.class })
    @Length(max = 50, message = "地理区域长度过长", groups = { AddGroup.class,
            UpdateGroup.class })
    private String area;
    
    /**
     * 联系人名称
     **/
    @Column(name = "CONTACTS")
    @NotEmpty(message = "联系人名称不能 为空", groups = { AddGroup.class,
            UpdateGroup.class })
    @Length(max = 20, message = "联系人名称长度过长", groups = { AddGroup.class,
            UpdateGroup.class })
    private String contacts;
    
    /**
     * 节能标识号
     **/
    @Column(name = "ENERGY_NO")
    @Length(max = 15, message = "节能标识号长度过长", groups = { AddGroup.class,
            UpdateGroup.class })
    private String energyNo;
    
    /**
     * 联系人手机
     **/
    @Column(name = "PHONE_NO")
    @Length(max = 11, message = "联系人手机长度过长", groups = { AddGroup.class,
            UpdateGroup.class })
    private String phoneNo;
    
    /**
     * 公司地址
     **/
    @Column(name = "ADDRESS")
    @Length(max = 100, message = "公司地址长度过长", groups = { AddGroup.class,
            UpdateGroup.class })
    private String address;
    
    /**
     * 联系电话
     **/
    @Column(name = "TEL_NUM")
    @Length(max = 13, message = "联系电话长度过长", groups = { AddGroup.class,
            UpdateGroup.class })
    private String telNum;
    
    /**
     * 注册日期
     **/
    @Column(name = "REG_DATE")
    @NotNull(message = "注册日期不能为空", groups = { AddGroup.class })
    private Date regDate;
    
    /**
     * 认证日期
     **/
    @Column(name = "AUTHOR_DATE")
    private Date authorDate;
    
    /**
     * 账户状态
     **/
    @Column(name = "STATE")
    @NotNull(message = "账户状态不能为空", groups = { AddGroup.class })
    private MemberStatesEnum state;
    
    /**
     * 审核状态
     **/
    @Column(name = "CHECK_STATE")
    private MemberCheckStateEnum checkState;
    
    /**
     * 总交易额
     **/
    @Column(name = "TOTAL_AMOUNT")
    private Long totalAmount;
    
    /**
     * 子账户数目
     **/
    @Column(name = "ACCOUNT_NUM")
    private Integer accountNum;
    
    /**
     * 营业执照图片path
     **/
    @Column(name = "B_LICENSE_PATH")
    private String bLicensePath;
    
    /**
     * 节能标识图片path
     **/
    @Column(name = "ENERGY_PATH")
    private String energyPath;
    
    /**
     * 邮政编码
     */
    @Column(name = "CODE")
    private String code;
    
    /**
     * 是否完善会员资料标记
     * 
     * @return
     */
    @Column(name = "FLAG")
    private Boolean flag;
    
    /**
     * 扫描设备号
     * 
     * @return
     */
    @Column(name = "DEVICE_NUMBER")
    private String deviceNumber;
    
    @Column(name = "VERSION_LOCK")
    @Version
    private Long version;
    
    /**
     * 资质证书
     */
    @Column(name = "QUALIFICATION_NO")
    private String qualification_no;
    
    /**
     * 资质证书图片地址
     */
    @Column(name = "QUALIFICATION_IMG")
    private String qualification_img;
    
    /**
     * 安全许可证
     */
    @Column(name = "SAFETY_LICENSES_NO")
    private String safety_licenses_no;
    
    /**
     * 安全许可证图片地址
     */
    @Column(name = "SAFETY_LICENSES_IMG")
    private String safety_licenses_img;
    
    /**
     * 质量管理体系认证证书
     */
    @Column(name = "QMS_NO")
    private String qms_no;
    
    /**
     * 质量管理体系认证证书图片地址
     */
    @Column(name = "QMS_IMG")
    private String qms_img;
    
    /**
     * 3C认证
     */
    @Column(name = "CCC_NO")
    private String ccc_no;
    
    /**
     * 3C认证图片
     */
    @Column(name = "CCC_IMG")
    private String ccc_img;
    
    /**
     * 工业产品生产许可证 
     */
    @Column(name = "PRODUCTION_LICENSE_NO")
    private String production_license_no;
    
    /**
     * 工业产品生产许可证图片
     */
    @Column(name = "PRODUCTION_LICENSE_IMG")
    private String production_license_img;
    
    /**
     * 法人
     */
    @Column(name = "LEGAL_REP")
    private String legalRep;
    
    /**
     * 注册资本
     */
    @Column(name = "REGISTERED_CAPITAL")
    private Double registeredCapital;
    
    /**
     * 成立日期
     */
    @Column(name = "FOUNDED_DATE")
    private Date foundedDate;
    
    /**
     * 资质等级
     */
    @Column(name = "LEVEL")
    private LevelEnum level;
    
    /**
     * 市场专员
     */
    @Column(name = "MARKETERS")
    private String marketers;
    
    /**
     * 备注
     */
    @Column(name = "REMARK")
    private String remark;
    
    public String getRemark()
    {
        return remark;
    }
    
    public void setRemark(String remark)
    {
        this.remark = remark;
    }
    
    public String getMarketers()
    {
        return marketers;
    }
    
    public void setMarketers(String marketers)
    {
        this.marketers = marketers;
    }
    
    public String getLegalRep()
    {
        return legalRep;
    }
    
    public void setLegalRep(String legalRep)
    {
        this.legalRep = legalRep;
    }
    
    public Double getRegisteredCapital()
    {
        return registeredCapital;
    }
    
    public void setRegisteredCapital(Double registeredCapital)
    {
        this.registeredCapital = registeredCapital;
    }
    
    public Date getFoundedDate()
    {
        return foundedDate;
    }
    
    public void setFoundedDate(Date foundedDate)
    {
        this.foundedDate = foundedDate;
    }
    
    public LevelEnum getLevel()
    {
        return level;
    }
    
    public void setLevel(LevelEnum level)
    {
        this.level = level;
    }
    
    public String getQualification_no()
    {
        return qualification_no;
    }
    
    public void setQualification_no(String qualification_no)
    {
        this.qualification_no = qualification_no;
    }
    
    public String getQualification_img()
    {
        return qualification_img;
    }
    
    public void setQualification_img(String qualification_img)
    {
        this.qualification_img = qualification_img;
    }
    
    public String getSafety_licenses_no()
    {
        return safety_licenses_no;
    }
    
    public void setSafety_licenses_no(String safety_licenses_no)
    {
        this.safety_licenses_no = safety_licenses_no;
    }
    
    public String getSafety_licenses_img()
    {
        return safety_licenses_img;
    }
    
    public void setSafety_licenses_img(String safety_licenses_img)
    {
        this.safety_licenses_img = safety_licenses_img;
    }
    
    public String getQms_no()
    {
        return qms_no;
    }
    
    public void setQms_no(String qms_no)
    {
        this.qms_no = qms_no;
    }
    
    public String getQms_img()
    {
        return qms_img;
    }
    
    public void setQms_img(String qms_img)
    {
        this.qms_img = qms_img;
    }
    
    public String getCcc_no()
    {
        return ccc_no;
    }
    
    public void setCcc_no(String ccc_no)
    {
        this.ccc_no = ccc_no;
    }
    
    public String getCcc_img()
    {
        return ccc_img;
    }
    
    public void setCcc_img(String ccc_img)
    {
        this.ccc_img = ccc_img;
    }
    
    public String getProduction_license_no()
    {
        return production_license_no;
    }
    
    public void setProduction_license_no(String production_license_no)
    {
        this.production_license_no = production_license_no;
    }
    
    public String getProduction_license_img()
    {
        return production_license_img;
    }
    
    public void setProduction_license_img(String production_license_img)
    {
        this.production_license_img = production_license_img;
    }
    
    public Long getVersion()
    {
        return version;
    }
    
    public void setVersion(Long version)
    {
        this.version = version;
    }
    
    public String getDeviceNumber()
    {
        return deviceNumber;
    }
    
    public void setDeviceNumber(String deviceNumber)
    {
        this.deviceNumber = deviceNumber;
    }
    
    public Boolean getFlag()
    {
        return flag;
    }
    
    public void setFlag(Boolean flag)
    {
        this.flag = flag;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getbLicensePath()
    {
        return bLicensePath;
    }
    
    public void setbLicensePath(String bLicensePath)
    {
        this.bLicensePath = bLicensePath;
    }
    
    public String getEnergyPath()
    {
        return energyPath;
    }
    
    public void setEnergyPath(String energyPath)
    {
        this.energyPath = energyPath;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getbLicenseNo()
    {
        return bLicenseNo;
    }
    
    public void setbLicenseNo(String bLicenseNo)
    {
        this.bLicenseNo = bLicenseNo;
    }
    
    public String getArea()
    {
        return area;
    }
    
    public void setArea(String area)
    {
        this.area = area;
    }
    
    public String getContacts()
    {
        return contacts;
    }
    
    public void setContacts(String contacts)
    {
        this.contacts = contacts;
    }
    
    public String getEnergyNo()
    {
        return energyNo;
    }
    
    public void setEnergyNo(String energyNo)
    {
        this.energyNo = energyNo;
    }
    
    public String getPhoneNo()
    {
        return phoneNo;
    }
    
    public void setPhoneNo(String phoneNo)
    {
        this.phoneNo = phoneNo;
    }
    
    public String getAddress()
    {
        return address;
    }
    
    public void setAddress(String address)
    {
        this.address = address;
    }
    
    public String getTelNum()
    {
        return telNum;
    }
    
    public void setTelNum(String telNum)
    {
        this.telNum = telNum;
    }
    
    public Date getRegDate()
    {
        return regDate;
    }
    
    public void setRegDate(Date regDate)
    {
        this.regDate = regDate;
    }
    
    public Date getAuthorDate()
    {
        return authorDate;
    }
    
    public void setAuthorDate(Date authorDate)
    {
        this.authorDate = authorDate;
    }
    
    public Long getTotalAmount()
    {
        return totalAmount;
    }
    
    public void setTotalAmount(Long totalAmount)
    {
        this.totalAmount = totalAmount;
    }
    
    public Integer getAccountNum()
    {
        return accountNum;
    }
    
    public void setAccountNum(Integer accountNum)
    {
        this.accountNum = accountNum;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getMemberNo()
    {
        return memberNo;
    }
    
    public void setMemberNo(String memberNo)
    {
        this.memberNo = memberNo;
    }
    
    public MemberStatesEnum getState()
    {
        return state;
    }
    
    public void setState(MemberStatesEnum state)
    {
        this.state = state;
    }
    
    public MemberCheckStateEnum getCheckState()
    {
        return checkState;
    }
    
    public void setCheckState(MemberCheckStateEnum checkState)
    {
        this.checkState = checkState;
    }
    
    public MemberTypeEnum getType()
    {
        return type;
    }
    
    public void setType(MemberTypeEnum type)
    {
        this.type = type;
    }
    
    public String getNoType()
    {
        return noType;
    }
    
    public void setNoType(String noType)
    {
        this.noType = noType;
    }
    
}