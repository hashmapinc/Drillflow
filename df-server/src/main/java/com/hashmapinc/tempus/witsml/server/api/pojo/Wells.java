/**
 * Copyright © 2018-2018 Hashmap, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hashmapinc.tempus.witsml.server.api.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Wells
{
	
    private String xmlns;
	
	
    private Well well;
	
	
    private String version;

    public String getXmlns ()
    {
        return xmlns;
    }
    
    @XmlAttribute
    public void setXmlns (String xmlns)
    {
        this.xmlns = xmlns;
    }

    public Well getWell ()
    {
        return well;
    }
    
    @XmlElement
    public void setWell (Well well)
    {
        this.well = well;
    }

    public String getVersion ()
    {
        return version;
    }
    @XmlAttribute
    public void setVersion (String version)
    {
        this.version = version;
    }
}

