/**
 * Copyright 2014 Nikita Koksharov, Nickolay Borbit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sxj.redis.advance.core;

/**
 * Base interface for all Redisson objects
 *
 * @author Nikita Koksharov
 *
 */
public interface RObject
{
    
    /**
     * Returns name of object
     *
     * @return name
     */
    String getName();
    
    /**
     * Deletes the object
     */
    void delete();
    
}
