

[<img src="https://s3.us-east-2.amazonaws.com/hm-witsml-server/drillFlowLogo.png" alt="DrillFlow"/>][drillflow]


[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1b2e8970193c426ab077ad29beb312c1)](https://app.codacy.com/app/cherrera2001/WitsmlApi-Server?utm_source=github.com&utm_medium=referral&utm_content=hashmapinc/WitsmlApi-Server&utm_campaign=Badge_Grade_Dashboard)
[![Build Status](https://img.shields.io/travis/hashmapinc/WitsmlApi-Server.svg?logo=travis)](https://travis-ci.org/hashmapinc/WitsmlApi-Server)
[![Documentation Status](https://readthedocs.org/projects/witsml-server-api/badge/?version=latest)](https://witsml-server-api.readthedocs.io/en/latest/?badge=latest)
![Docker Build Status](https://img.shields.io/docker/build/hashmapinc/witsmlapi-server.svg?logo=docker)
[![Waffle.io - Columns and their card count](https://badge.waffle.io/hashmapinc/WitsmlApi-Server.svg?columns=Backlog,Done,In%20progress)](https://waffle.io/hashmapinc/WitsmlApi-Server)

A dockerized WITSML API Server that is agnostic of the backend.

## Table of Contents

-   [Features](#features)
-   [Requirements](#requirements)
-   [Getting Started](#getting-started)
-   [Getting Help](#getting-help)
-   [Documentation](#documentation)
-   [License](#license)
-   [Export Control](#export-control)

## Features

## Requirements

-   JDK 11 
-   Apache Maven 3.3 or higher
-   Git Client
-   Docker (To build the docker image)

## Getting Started

### Building

Execute:

```bash
mvn clean install
```

### Running

Execute:

```bash
java -jar target/server-0.0.1-SNAPSHOT.jar
```

### Testing

By default the service will be available at:

`http://localhost:7070/Service/WMLS`

Execute the following SOAP query to get the version:
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:tns="http://www.witsml.org/wsdl/120" xmlns:types="http://www.witsml.org/wsdl/120/encodedTypes" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
   <soap:Body soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
     <q1:WMLS_GetVersion xmlns:q1="http://www.witsml.org/message/120" />
   </soap:Body>
 </soap:Envelope>
```
 
By default the server should return the following response:
 
 ```xml
 <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
      <ns1:WMLS_GetVersionResponse xmlns:ns1="http://www.witsml.org/wsdl/120">
        <return xmlns:ns2="http://www.witsml.org/wsdl/120">1.3.1.1,1.4.1.1</return>
      </ns1:WMLS_GetVersionResponse>
    </soap:Body>
  </soap:Envelope>
  ```

### Building the Docker image

Navigate to the docker directory

Execute `docker build . -t hashmapinc/witsmlapi-server:latest` to build the image

Once completed execute `docker run -p 7070:7070 hashmapinc/witsmlapi-server:latest` 

## Getting Help
You can also submit issues or questions via GitHub Issues [here](https://github.com/hashmapinc/WitsmlApi-Server/issues)

## Documentation

See [The Documentation Here](https://witsml-server-api.readthedocs.io/en/latest/) for the latest updates.

## License

Except as otherwise noted this software is licensed under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Export Control

This distribution includes cryptographic software. The country in which you
currently reside may have restrictions on the import, possession, use, and/or
re-export to another country, of encryption software. BEFORE using any
encryption software, please check your country's laws, regulations and
policies concerning the import, possession, or use, and re-export of encryption
software, to see if this is permitted. See <http://www.wassenaar.org/> for more
information.

The U.S. Government Department of Commerce, Bureau of Industry and Security
(BIS), has classified this software as Export Commodity Control Number (ECCN)
5D002.C.1, which includes information security software using or performing
cryptographic functions with asymmetric algorithms. The form and manner of this
distribution makes it eligible for export under the
License Exception ENC Technology Software Unrestricted (TSU) exception (see the
BIS Export Administration Regulations, Section 740.13) for both object code and
source code.

The following provides more details on the included cryptographic software:

This project uses BouncyCastle and the built-in
java cryptography libraries for SSL, SSH via CXF. See
[http://bouncycastle.org/about.html](http://bouncycastle.org/about.html)
[http://www.oracle.com/us/products/export/export-regulations-345813.html](http://www.oracle.com/us/products/export/export-regulations-345813.html)
for more details on each of these libraries cryptography features.
