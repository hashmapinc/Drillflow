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
package com.hashmapinc.tempus.witsml.server.api;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsCommonData;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsGeodeticModel;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLocalCRS;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLocation;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsProjectionx;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsReferencePoint;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsStnTrajCorUsed;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsStnTrajMatrixCov;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsStnTrajRawData;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsStnTrajValid;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsTrajectoryStation;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsWellCRS;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsWellDatum;
import com.hashmapinc.tempus.WitsmlObjects.v1311.DimensionlessMeasure;
import com.hashmapinc.tempus.WitsmlObjects.v1311.MeasuredDepthCoord;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore;
import com.hashmapinc.tempus.WitsmlObjects.v1311.TimeMeasure;
import com.hashmapinc.tempus.WitsmlObjects.v1311.WellElevationCoord;
import com.hashmapinc.tempus.WitsmlObjects.v1311.WellVerticalDepthCoord;
import com.hashmapinc.tempus.WitsmlObjects.v20.WellDatum;
import com.hashmapinc.tempus.witsml.WitsmlException;
import com.hashmapinc.tempus.witsml.WitsmlObjectParser;
import com.hashmapinc.tempus.witsml.WitsmlUtil;
import com.hashmapinc.tempus.witsml.server.api.QueryValidation.ERRORCODE;

interface Validation extends Function<ValidateParam, ValidationResult> {

	static final Logger LOG = Logger.getLogger(Validation.class.getName());
	public static StoreImpl store = new StoreImpl();

	public static String uidExpression = "//*[@uid]";
	public static String uomExpression = "//*[@uom]";
	public static String WELL_XML_TAG = "well";
	public static String WELLS_XML_TAG = "wells";
	public static String LOG_XML_TAG = "<logData>";
	public static String uidAttribute = "uid";
	public static String uomAttribute = "uom";
	public static List<AbstractWitsmlObject> witsmlObjects = null;
	public static String version = null;
	public static String uom1311units = "1/H,1/K,1/kg,1/m,1/m2,1/m3,1/N,1/Pa,1/s,1/V,A,A.m2,A/m,A/m2,10 dB,10 dB/m,1/s,Bq,Bq/kg,C,C.m,C/kg,C/m2,C/m3,cd,cd/m2,eq,eq/kg,eq/m3,100%,F,F/m,gAPI,Gy,H,H/m,Hz,1/s,c/s,J,J/K,J/kg,J/kg.K,J/m3,J/mol,J/mol.K,K,K.m2/W,K/m,K/s,K/W,kg,kg.m,kg.m/s,kg.m2,kg/J,kg/m,kg/m2,kg/m2.s,kg/m3,kg/m4,kg/s,lm,lm.s,lm/W,lx,lx.s,m,m/K,m/s,m/s2,m2,m2/kg,m2/mol,m2/Pa.s,m2/s,m3,m3/J,m3/kg,m3/mol,m3/Pa.s,m3/Pa/s,m3/Pa/s,m3/Pa.s,m3/Pa2.s2,m3/s,m3/s2,m4,m4/s,mol,mol/m2,mol/m2.s,mol/m3,mol/s,N,N.m2,N/m,N/m3,N4/kg.m7,nAPI,O,ohm,ohm.m,ohm/m,Pa,Pa.s,Pa.s/m3,Pa.s/m6,Pa/m,Pa/m3,Pa/s,Pa2,rad,rad/m,rad/m3,rad/s,rad/s2,S,s,S/m,s/m,s/m3,sr,Sv,T,V,V/B,V/m,W,W/K,W/m.K,W/m2,W/m2.K,W/m2.sr,W/m3,W/m3.K,W/sr,Wb,Wb.m,Wb/m,%,cEuc,pu,1/a,1/bar,1/bbl,1/cm,1/d,1/degC,1/degF,1/degR,1/ft,1/ft2,1/ft3,1/g,1/galUK,1/galUS,1/h,1/in,1/km2,1/kPa,1/L,1/lbf,1/lbm,1/mi,1/mi2,1/min,1/mm,1/nm,1/pPa,1/psi,1/upsi,1/uV,km3/(d.m),km3/(h.m),yr(100k),a,A.h,A/cm2,A/ft2,A/mm,A/mm2,acre,acre.ft,ag,aJ,atm,atm/ft,atm/h,atm/m,b,b/cm3,bar,bar/h,bar/km,bar/m,bar2,bar2/cP,bbl,bbl/acre,bbl/acre.ft,bbl/bbl,bbl/cP.d.psi,bbl/d,bbl/d.acre.ft,bbl/d.ft,bbl/(d.ft),bbl/d.ft.psi,bbl/d.psi,bbl/psi.d,bbl/d2,bbl/ft,bbl/ft3,bbl/hr,bbl/hr2,bbl/in,bbl/kPa.d,bbl/(d.kPa),bbl/mi,bbl/min,bbl/psi.d,bbl/(d.psi),bbl/d.psi,bbl/tonUK,bbl/tonUS,Btu,Btu.in/h.ft2.degF,Btu/bbl,Btu/ft3,Btu/galUK,Btu/galUS,Btu/h,Btu/h.ft.degF,Btu/h.ft2,Btu/h.ft2.degF,Btu/hr.ft2.degR,Btu/h.ft3,Btu/h.ft3.degF,Btu/h.m2.degC,Btu/lbm,Btu/lbm.degF,Btu/lbm.degR,Btu/min,Btu/s,Btu/s.ft2,Btu/s.ft2.degF,Btu/s.ft3,Btu/s.ft3.degF,c,C/cm2,C/cm3,C/g,C/mm2,C/mm3,c/s,rev/s,cal,cal/cm3,cal/g,cal/g.K,cal/h.cm.degC,cal/h.cm2,cal/h.cm2.degC,cal/h.cm3,cal/kg,cal/lbm,cal/mL,cal/mm3,cal/s.cm.degC,cal/s.cm2.degC,cal/s.cm3,%,%,Ci,curie,cm,cm/a,cm/s,cm/s2,cm2,cm2/g,cm2/s,cm3,cm3/cm3,cm3/g,cm3/h,cm3/m3,cm3/min,cm3/s,cm4,cP,cu,ft3,ft3(std,60F),in3,in3,1/27 ft3,yd3,mi3,mi3,Ci,Ci,D,d,D.ft,D.m,d/bbl,d/ft3,d/m3,dAPI,dB,dB/ft,dB/m,degC,degF,K,degR,dega,dega/ft(100),dega/ft,dega/100ft,dega/h,dega/m,dega/min,dega/s,degC,degC.m2.h/kcal,degC/ft,degC/h,degC/km,degC/m,degC/min,degC/s,degF,degF.ft2.h/Btu,degF/ft(100),degF/ft,degF/100ft,degF/h,degF/m,degF/min,degF/s,degR,dm,dm/s,dm3,L,L,dm3/km(100),dm3/kg,dm3/100km,dm3/kW.h,dm3/m,dm3/m3,dm3/MJ,dm3/mol,dm3/s,dm3/s2,dm3/t,dN.m,.1 Pa,(dyne/cm)4/gcm3,(N/m)4/kg.m3,ehp,EJ,EJ/a,eq/L,mN/m,eV,fC,flozUK,flozUS,1/s,flops,fl ozUK,fl ozUS,fm,ft,ft.lbf,ft.lbf/bbl,ft.lbf/galUS,ft.lbf/lbm,ft.lbf/min,ft.lbf/s,ft.lbm,ft/bbl,ft/d,ft/degF,ft/ft,ft/ft3,ft/galUS,ft/h,ft/in,ft/m,ft/mi,ft/min,ft/ms,ft/s,ft/s2,ft/us,ft2,sq ft,ft2/h,ft2/in3,ft2/s,ft3,cu ft,scf(60F),ft3/bbl,ft3/d,ft3/d.ft.psi,ft3/d2,ft3/ft,ft3/ft3,ft3/h,ft3/h2,ft3/kg,ft3/lbm,ft3/min,ft3/min.ft2,ft3/min2,ft3/s,ft3/s.ft2,ft3/s2,ftCla,ftAM,ftUS,g,g.ft/cm3.s,g/cm3,g/cm4,g/dm3,g/galUK,g/galUS,g/kg,g/L,g/dm3,g/m3,g/s,galUK,galUK/d,galUK/ft3,galUK/h,galUK/h.ft,galUK/h.ft2,galUK/h.in,galUK/h.in2,galUK/h2,galUK/lbm,galUK/mi,galUK/min,galUK/min.ft,galUK/min.ft2,galUK/min2,galUS,galUS/bbl,galUS/d,galUS/ft,galUS/ft3,galUS/h,galUS/h.ft,galUS/h.ft2,galUS/h.in,galUS/h.in2,galUS/h2,galUS/lbm,galUS/mi,galUS/min,galUS/min.ft,galUS/min.ft2,galUS/min2,galUS/tonUK,galUS/tonUS,GBq,GeV,gf,GHz,GJ,gn,Gohm,GPa,GPa/cm,GPa2,grad,Grad,grain/ft3(100),grain/100ft3,GS,GW,GW.h,h,h/ft3,h/km,h/m3,ha,ha.m,hbar,hhp,hhp/in2,hL,hp,hp.h,hp.h/bbl,hp.h/lbm,hp/ft3,hp/in2,in,1/16 in,1/32 in,1/64 in,in/a,in/in.degF,in/min,in/s,in2|sq in,sq in,in2/ft2,in2/in2,in2/s,in3|cu in,cu in,in3/ft,in4,inUS,J/cm2,J/dm3,J/g,J/g.K,J/m,J/m2,J/s.m2.degC,K.m2/kW,kA,Mbbl/d,kC,kcal,kcal.m/cm2,kcal/cm3,kcal/g,kcal/h,kcal/h.m.degC,kcal/h.m2.degC,kcal/kg,kcal/kg.degC,kcal/m3,kcd,keV,kg.m/cm2,kg/d,kg/dm3,kg/dm4,kg/h,kg/kg,kg/kW.h,kg/L,kg/dm3,kg/m.s,kg/min,kg/MJ,kgf,kgf.m,kgf.m/cm2,kgf.m/m,kgf.m2,kgf.s/m2,kgf/cm,kgf/cm2,kgf/kgf,kgf/mm2,kHz,kJ,kJ.m/h.m2.K,kJ/dm3,kJ/h.m2.K,kJ/kg,kJ/kg.K,kJ/m3,kJ/mol,kJ/mol.K,klx,km,km/cm,km/dm3,km/h,km/L,km/s,km2,km3,kmol,kN,kN.m,kN.m2,kN/m,kN/m2,kohm,kohm.m,kPa,kPa.s/m,kPa/hm,kPa/h,kPa/m,kPa/min,kPa2,kPa2/cP,kS,kV,kW,kW.h,kW.h/dm3,kW.h/kg,kW.h/kg.degC,kW.h/m3,kW/cm2,kW/m2,kW/m2.K,kW/m3,kW/m3.K,L,dm3,dm3,L/km(100),(L/min)/bar,L/h,L/kg,L/100km,L/m,L/m3,L/min,L/mol,L/s,L/s2,L/t,L/tonUK,lbf,lbf.ft,lbf.ft/bbl,lbf.ft/in,lbf.ft/in2,lbf.ft/lbm,lbf.in,lbf.in/in,lbf.in2,lbf.s/ft2,lbf.s/in2,lbf/ft2(100),lbf/ft,lbf/ft2,lbf/100ft2,lbf/ft3,lbf/galUS,lbf/in,lbf/in2,lbf/lbf,lbm,lbm.ft/s,lbm.ft2,lbm.ft2/s2,Mlbm/yr,lbm/galUK(1000),lbm/galUS(1000),lbm/bbl,lbm/d,lbm/ft,lbm/h.ft,lbm/h.ft,lbm/s.ft,lbm/s.ft2,lbm/ft2,lbm/ft3,lbm/ft4,lbm/galUK,lbm/galUK.ft,lbm/1000galUK,lbm/galUS,lbm/galUS.ft,lbm/1000galUS,lbm/h,lbm/h.ft,lbm/ft.h,lbm/h.ft2,lbm/hp.h,lbm/in3,lbm/min,lbm/s,lbm/s.ft,lbm/ft.s,lbm/s.ft2,lm/m2,lm/m2,m/cm,m/d,m/h,m/km,m/m,m/m.K,m/m3,m/min,m/ms,m2/cm3,m2/d.kPa,m2/g,m2/h,m2/m2,m2/m3,m3/bar.d,m3/(d.bar),m3/bar.d,m3.bar.h,m3/(h.bar),m3/bar.h,m3/bar.min,m3/(min.bar),m3/bar.min,m3/cP.d.kPa,m3/cP.Pa.s,m3/d,m3/d.kPa,m3/d.kPa,m3/kPa.d,m3/(d.kPa),m3/d.m,m3/(d.m),m3/d2,m3/g,m3/h,m3/h.m,m3/(h.m),m3/ha.m,m3/km,m3/kPa.d,m3/(d.kPa),m3/d.kPa,m3/k.kPa,m3/kPa.h,(m3/h)/kPa,m3/kW.h,m3/m,m3/m2,m3/m3,m3/min,m3/mol(kg),m3/mol,m3/psi.d,m3/(d.psi),m3/s.ft,m3/(s.ft),m3/s.m,m3/s.m2,m3/t,m3/tonUK,m3/tonUS,mA,Ma,MA,mA/cm2,mA/ft2,mbar,Mbbl,Mbbl.ft/d,kbbl/d,MBq,Mbyte,mC,mC/m2,mCi,mcurie,mCi,mD,mD.ft,mD.ft2/lbf.s,mD.in2/lbf.s,mD.m,mD/cP,mD/Pa.s,meq,meq/cm3,meq/g,ppk,ppk,permil,MeV,Mg,mg,Mg/a,Mg/d,mg/dm3,mg/galUS,Mg/h,Mg/in,mg/J,mg/kg,mg/L,Mg/m2,mg/m3,Mg/m3,mGal,mgn,mGy,mH,mho,mho/m,MHz,mHz,mi,mi/galUK,mi/galUS,mi/h,mi/in,mi2,sq mi,mi3,cubem,in/1000,min,min/ft,min/m,mina,miUS,miUS2,MJ,mJ,MJ/a,mJ/cm2,MJ/kg,MJ/m,mJ/m2,MJ/m3,MJ/mol,mL,mL/galUK,mL/galUS,mL/mL,mm,Mm,mm/a,mm/mm.K,mm/s,mm2,mm2/mm2,mm2/s,mm3,mm3/J,M(ft3),mmho/m,mmol,Mscm3,MN,mN,mN.m2,mN/km,mN/m,Mohm,mohm,mol,mol/h,mol/m3,mol/s,mPa,MPa,mPa.s,MPa.s/m,MPa/h,MPa/m,Mpsi,mrad,Mrad,mS,ms,ms/cm,ms/ft,ms/in,mS/m,ms/m,ms/s,MMscm(15C),mSv,mT,MV,mV,mV/ft,mV/m,MW,mW,MW.h,MW.h/kg,MW.h/m3,mW/m2,mWb,MA,Ma,N.m,N.m/m,N.s/m2,N/m2,N/mm2,nA,nC,nCi,ncurie,nCi,nH,nJ,nm,nm/s,nohm,ns,ns/ft,ns/m,nT,nW,Oe,ohm.cm,ozf,ozm,P,pA,Pa.s2/m3,Pa/h,pC,pCi,pcurie,pCi/g,pCi,pCi,ppk,permil,mEuc,pF,pm,pPa,ppk,permil,mEuc,ppm,uEuc,ppm/degC,ppm/degF,ps,pS,lbf/ft2,lbf/ft3,psi,lbf/in2,lbf/in2,psi.d/bbl,psi.s,psi/ft(100),psi/ft,psi/100ft,psi/h,psi/m,psi/min,psi2,psi2.d/cP.ft3,psi2.d/cp.ft3,psi2.d/cP.ft3,psi2.d/cP.ft3,psi2.d2/cP.ft6,psi2.d2/cp.ft6,psi2.d2/cP.ft6,psi2.d2/cP.ft6,psi2/cP,rad/ft,rad/ft3,rpm,c/s,c/s,rev/min,s/cm,s/ft,s/ft3,s/in,s/L,scf(60F),seca,cu,cu,ft2,ft2,in2,in2,mi2,mi2,yd2,yd2,t,t/a,t/d,t/h,t/min,lm.s,TBq,TeV,therm/galUK,therm/lbm,TJ,TJ/a,Tohm,tonfUS.mi,tonUK,tonUK/a,tonUK/d,tonUK/h,tonUK/min,tonUS,tonUS/a,tonUS/d,tonUS/ft2,tonUS/h,tonUS/min,TW,TW.h,uA,uA/cm2,uA/in2,ubar,uC,uCi,ucurie,uCi,ppm,uEuc,uF,uF/m,ug,ug/cm3,uH,uH/m,uHz,uJ,um,um/s,um2,um2.m,umol,uN,Euc,uohm,uohm/ft,uohm/m,uPa,upsi,urad,uS,us,us/ft,us/m,uT,uV,uV/ft,uV/m,uW,uW/m3,uWb,V/dB,%,%,cEuc,ppm,ppm,W/cm2,W/kW,W/mm2,W/W,Wb/mm,%,%,cEuc,ppm,ppm,uEuc,1/27 ft3";
	public static String uom1411units = "1/H,1/K,1/kg,1/m,1/m2,1/m3,1/N,1/Pa,1/s,1/V,A,A.m2,A/m,A/m2,10 dB,10 dB/m,1/s,Bd,Bq,Bq/kg,C,C.m,C/kg,C/m2,C/m3,cd,cd/m2,eq,eq/kg,eq/m3,100%,F,F/m,gAPI,Gy,H,H/m,Hz,1/s,c/s,J,J/K,J/kg,J/kg.K,J/m3,J/mol,J/mol.K,K,K.m2/W,K/m,K/s,K/W,kg,kg.m,kg.m/s,kg.m2,kg/J,kg/m,kg/m2,kg/m2.s,kg/m3,kg/m4,kg/s,lm,lm.s,lm/W,lx,lx.s,m,m/K,m/s,m/s2,m2,m2/kg,m2/mol,m2/Pa.s,m2/s,m3,m3/J,m3/kg,m3/mol,m3/Pa.s,m3/Pa/s,m3/Pa/s,m3/Pa.s,m3/Pa2.s2,m3/s,m3/s2,m4,m4/s,mol,mol/m2,mol/m2.s,mol/m3,mol/s,N,N.m2,N/m,N/m3,N4/kg.m7,nAPI,O,ohm,ohm.m,ohm/m,Pa,Pa.s,Pa.s/m3,Pa.s/m6,Pa/m,Pa/m3,Pa/s,Pa2,rad,rad/m,rad/m3,rad/s,rad/s2,S,s,S/m,s/m,s/m3,sr,Sv,T,V,V/B,V/m,W,W/K,W/m.K,W/m2,W/m2.K,W/m2.sr,W/m3,W/m3.K,W/sr,Wb,Wb.m,Wb/m,%,cEuc,pu,1/a,1/bar,1/bbl,1/cm,1/d,1/degC,1/degF,1/degR,1/ft,1/ft2,1/ft3,1/g,1/galUK,1/galUS,1/h,1/in,1/km2,1/kPa,1/L,1/lbf,1/lbm,1/mi,1/mi2,1/min,1/mm,1/nm,1/pPa,1/psi,1/upsi,1/uV,km3/(d.m),km3/(h.m),yr(100k),a,A.h,A/cm2,A/ft2,A/mm,A/mm2,acre,acre.ft,ag,aJ,atm,atm/ft,atm/h,atm/m,b,b/cm3,bar,bar/h,bar/km,bar/m,bar2,bar2/cP,bbl,bbl/acre,bbl/acre.ft,bbl/bbl,bbl/cP.d.psi,bbl/d,bbl/d.acre.ft,bbl/d.ft,bbl/(d.ft),bbl/d.ft.psi,bbl/d.psi,bbl/psi.d,bbl/d2,bbl/ft,bbl/ft3,bbl/hr,bbl/hr2,bbl/in,bbl/kPa.d,bbl/(d.kPa),bbl/mi,bbl/min,bbl/psi.d,bbl/(d.psi),bbl/d.psi,bbl/tonUK,bbl/tonUS,Btu,Btu.in/h.ft2.degF,Btu/bbl,Btu/ft3,Btu/galUK,Btu/galUS,Btu/h,Btu/h.ft.degF,Btu/h.ft2,Btu/h.ft2.degF,Btu/hr.ft2.degR,Btu/h.ft3,Btu/h.ft3.degF,Btu/h.m2.degC,Btu/lbm,Btu/lbm.degF,Btu/lbm.degR,Btu/min,Btu/s,Btu/s.ft2,Btu/s.ft2.degF,Btu/s.ft3,Btu/s.ft3.degF,c,C/cm2,C/cm3,C/g,C/mm2,C/mm3,c/s,rev/s,cal,cal/cm3,cal/g,cal/g.K,cal/h.cm.degC,cal/h.cm2,cal/h.cm2.degC,cal/h.cm3,cal/kg,cal/lbm,cal/mL,cal/mm3,cal/s.cm.degC,cal/s.cm2.degC,cal/s.cm3,%,%,Ci,curie,cm,cm/a,cm/s,cm/s2,cm2,cm2/g,cm2/s,cm3,cm3/cm3,cm3/g,cm3/h,cm3/m3,cm3/min,cm3/s,cm4,cP,cu,ft3,ft3(std,60F),in3,in3,1/27 ft3,yd3,mi3,mi3,Ci,Ci,D,d,D.ft,D.m,d/bbl,d/ft3,d/m3,dAPI,dB,dB/ft,dB/m,dB/km,degC,degF,K,degR,dega,dega/ft(100),dega/ft,dega/100ft,dega/h,dega/m,dega/min,dega/s,degC,degC.m2.h/kcal,degC/ft,degC/h,degC/km,degC/m,degC/min,degC/s,degF,degF.ft2.h/Btu,degF/ft(100),degF/ft,degF/100ft,degF/h,degF/m,degF/min,degF/s,degR,dm,dm/s,dm3,L,L,dm3/km(100),dm3/kg,dm3/100km,dm3/kW.h,dm3/m,dm3/m3,dm3/MJ,dm3/mol,dm3/s,dm3/s2,dm3/t,dN.m,.1 Pa,(dyne/cm)4/gcm3,(N/m)4/kg.m3,ehp,EJ,EJ/a,eq/L,mN/m,eV,fC,flozUK,flozUS,1/s,flops,fl ozUK,fl ozUS,fm,ft,ft.lbf,ft.lbf/bbl,ft.lbf/galUS,ft.lbf/lbm,ft.lbf/min,ft.lbf/s,ft.lbm,ft/bbl,ft/d,ft/degF,ft/ft,ft/ft3,ft/galUS,ft/h,ft/in,ft/m,ft/mi,ft/min,ft/ms,ft/s,ft/s2,ft/us,ft2,sq ft,ft2/h,ft2/in3,ft2/s,ft3,cu ft,scf(60F),ft3/bbl,ft3/d,ft3/d.ft.psi,ft3/d2,ft3/ft,ft3/ft3,ft3/h,ft3/h2,ft3/kg,ft3/lbm,ft3/min,ft3/min.ft2,ft3/min2,ft3/s,ft3/s.ft2,ft3/s2,ftCla,ftAM,ftUS,g,g.ft/cm3.s,g/cm3,g/cm4,g/dm3,g/galUK,g/galUS,g/kg,g/L,g/dm3,g/m3,g/s,galUK,galUK/d,galUK/ft3,galUK/h,galUK/h.ft,galUK/h.ft2,galUK/h.in,galUK/h.in2,galUK/h2,galUK/lbm,galUK/mi,galUK/min,galUK/min.ft,galUK/min.ft2,galUK/min2,galUS,galUS/bbl,galUS/d,galUS/ft,galUS/ft3,galUS/h,galUS/h.ft,galUS/h.ft2,galUS/h.in,galUS/h.in2,galUS/h2,galUS/lbm,galUS/mi,galUS/min,galUS/min.ft,galUS/min.ft2,galUS/min2,galUS/tonUK,galUS/tonUS,GBq,GeV,gf,GHz,GJ,gn,Gohm,GPa,GPa/cm,GPa2,grad,Grad,grain/ft3(100),grain/100ft3,GS,GW,GW.h,h,h/ft3,h/km,h/m3,ha,ha.m,hbar,hhp,hhp/in2,hL,hp,hp.h,hp.h/bbl,hp.h/lbm,hp/ft3,hp/in2,in,1/16 in,1/32 in,1/64 in,in/a,in/in.degF,in/min,in/s,in2|sq in,sq in,in2/ft2,in2/in2,in2/s,in3|cu in,cu in,in3/ft,in4,inUS,J/cm2,J/dm3,J/g,J/g.K,J/m,J/m2,J/s.m2.degC,K.m2/kW,kA,Mbbl/d,kC,kcal,kcal.m/cm2,kcal/cm3,kcal/g,kcal/h,kcal/h.m.degC,kcal/h.m2.degC,kcal/kg,kcal/kg.degC,kcal/m3,kcd,keV,kg.m/cm2,kg/d,kg/dm3,kg/dm4,kg/h,kg/kg,kg/kW.h,kg/L,kg/dm3,kg/m.s,kg/min,kg/MJ,kgf,kgf.m,kgf.m/cm2,kgf.m/m,kgf.m2,kgf.s/m2,kgf/cm,kgf/cm2,kgf/kgf,kgf/mm2,kHz,kJ,kJ.m/h.m2.K,kJ/dm3,kJ/h.m2.K,kJ/kg,kJ/kg.K,kJ/m3,kJ/mol,kJ/mol,kJ/kmol.K,kJ/mol.K,klx,km,km/cm,km/dm3,km/h,km/L,km/s,km2,km3,kmol,kN,kN.m,kN.m2,kN/m,kN/m2,kohm,kohm.m,kPa,kPa.s/m,kPa/hm,kPa/h,kPa/m,kPa/min,kPa2,kPa2/cP,kS,kV,kW,kW.h,kW.h/dm3,kW.h/kg,kW.h/kg.degC,kW.h/m3,kW/cm2,kW/m2,kW/m2.K,kW/m3,kW/m3.K,L,dm3,dm3,L/km(100),(L/min)/bar,L/h,L/kg,L/100km,L/m,L/m3,L/min,L/mol,L/mol,L/s,L/s2,L/t,L/tonUK,lbf,lbf.ft,lbf.ft/bbl,lbf.ft/in,lbf.ft/in2,lbf.ft/lbm,lbf.in,lbf.in/in,lbf.in2,lbf.s/ft2,lbf.s/in2,lbf/ft2(100),lbf/ft,lbf/ft2,lbf/100ft2,lbf/ft3,lbf/galUS,lbf/in,lbf/in2,lbf/lbf,lbm,lbm.ft/s,lbm.ft2,lbm.ft2/s2,Mlbm/yr,lbm/galUK(1000),lbm/galUS(1000),lbm/bbl,lbm/d,lbm/ft,lbm/h.ft,lbm/h.ft,lbm/s.ft,lbm/s.ft2,lbm/ft2,lbm/ft3,lbm/ft4,lbm/galUK,lbm/galUK.ft,lbm/1000galUK,lbm/galUS,lbm/galUS.ft,lbm/1000galUS,lbm/h,lbm/h.ft,lbm/ft.h,lbm/h.ft2,lbm/hp.h,lbm/in3,lbm/min,lbm/s,lbm/s.ft,lbm/ft.s,lbm/s.ft2,lm/m2,lm/m2,m/cm,m/d,m/h,m/km,m/m,m/m.K,m/m3,m/min,m/ms,m2/cm3,m2/d.kPa,m2/g,m2/h,m2/m2,m2/m3,m3/bar.d,m3/(d.bar),m3/bar.d,m3.bar.h,m3/(h.bar),m3/bar.h,m3/bar.min,m3/(min.bar),m3/bar.min,m3/cP.d.kPa,m3/cP.Pa.s,m3/d,m3/d.kPa,m3/d.kPa,m3/kPa.d,m3/(d.kPa),m3/d.m,m3/(d.m),m3/d2,m3/g,m3/h,m3/h.m,m3/(h.m),m3/ha.m,m3/km,m3/kPa.d,m3/(d.kPa),m3/d.kPa,m3/k.kPa,m3/kPa.h,(m3/h)/kPa,m3/kW.h,m3/m,m3/m2,m3/m3,m3/min,m3/mol(kg),m3/mol,m3/mol(kg),m3/mol,m3/psi.d,m3/(d.psi),m3/s.ft,m3/(s.ft),m3/s.m,m3/s.m2,m3/t,m3/tonUK,m3/tonUS,mA,Ma,MA,mA/cm2,mA/ft2,mbar,Mbbl,Mbbl.ft/d,kbbl/d,MBq,Mbyte,mC,mC/m2,mCi,mcurie,mCi,mD,mD.ft,mD.ft2/lbf.s,mD.in2/lbf.s,mD.m,mD/cP,mD/Pa.s,meq,meq/cm3,meq/g,ppk,ppk,permil,MeV,Mg,mg,Mg/a,Mg/d,mg/dm3,mg/galUS,Mg/h,Mg/in,mg/J,mg/kg,mg/L,Mg/m2,mg/m3,Mg/m3,mGal,mgn,mGy,mH,mho,mho/m,MHz,mHz,mi,mi/galUK,mi/galUS,mi/h,mi/in,mi2,sq mi,mi3,cubem,in/1000,min,min/ft,min/m,mina,miUS,miUS2,MJ,mJ,MJ/a,mJ/cm2,MJ/kg,MJ/m,mJ/m2,MJ/m3,MJ/mol,MJ/mol,mL,mL/galUK,mL/galUS,mL/mL,mm,Mm,mm/a,mm/mm.K,mm/s,mm2,mm2/mm2,mm2/s,mm3,mm3/J,M(ft3),mmho/m,mmol,Mscm3,MN,mN,mN.m2,mN/km,mN/m,Mohm,mohm,mol,mol/h,mol/h,mol/m3,mol/m3,mol/s,mol/s,mPa,MPa,mPa.s,MPa.s/m,MPa/h,MPa/m,Mpsi,mrad,Mrad,mS,ms,ms/cm,ms/ft,ms/in,mS/m,ms/m,ms/s,MMscm(15C),mSv,mT,MV,mV,mV/ft,mV/m,MW,mW,MW.h,MW.h/kg,MW.h/m3,mW/m2,mWb,MA,Ma,N.m,N.m/m,N.s/m2,N/m2,N/mm2,nA,nC,nCi,ncurie,nCi,nH,nJ,nm,nm/s,nohm,ns,ns/ft,ns/m,nT,nW,Oe,ohm.cm,ozf,ozm,P,pA,Pa.s2/m3,Pa/h,pC,pCi,pcurie,pCi/g,pCi,pCi,ppk,permil,mEuc,pF,pm,pPa,ppk,permil,mEuc,ppm,uEuc,ppm/degC,ppm/degF,ps,pS,lbf/ft2,lbf/ft3,psi,lbf/in2,lbf/in2,psi.d/bbl,psi.s,psi/ft(100),psi/ft,psi/100ft,psi/h,psi/m,psi/min,psi2,psi2.d/cP.ft3,psi2.d/cp.ft3,psi2.d/cP.ft3,psi2.d/cP.ft3,psi2.d2/cP.ft6,psi2.d2/cp.ft6,psi2.d2/cP.ft6,psi2.d2/cP.ft6,psi2/cP,rad/ft,rad/ft3,rpm,c/s,c/s,rev/min,s/cm,s/ft,s/ft3,s/in,s/L,scf(60F),seca,cu,cu,ft2,ft2,in2,in2,mi2,mi2,yd2,yd2,t,t/a,t/d,t/h,t/min,lm.s,TBq,TeV,therm/galUK,therm/lbm,TJ,TJ/a,Tohm,tonfUS.mi,tonUK,tonUK/a,tonUK/d,tonUK/h,tonUK/min,tonUS,tonUS/a,tonUS/d,tonUS/ft2,tonUS/h,tonUS/min,TW,TW.h,uA,uA/cm2,uA/in2,ubar,uC,uCi,ucurie,uCi,ppm,uEuc,uF,uF/m,ug,ug/cm3,uH,uH/m,uHz,uJ,um,um/s,um2,um2.m,umol,uN,Euc,uohm,uohm/ft,uohm/m,uPa,upsi,urad,uS,us,us/ft,us/m,uT,uV,uV/ft,uV/m,uW,uW/m3,uWb,V/dB,%,%,cEuc,ppm,ppm,W/cm2,W/kW,W/mm2,W/W,Wb/mm,%,%,cEuc,ppm,ppm,uEuc,1/27 ft3";

	static Validation error401() {
		return holds(param -> !checkWell(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_401.value());
	}

	static Validation error402() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_402.value());
	}

	static Validation error403() {
		return holds(param -> !checkNameSpace(param.getXMLin()), ERRORCODE.ERROR_403.value());
	}

	static Validation error404() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_404.value());
	}

	static Validation error406() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_406.value());
	}

	static Validation error407() {
		return holds(param -> !checkWMLTypeEmpty(param.getWMLtypeIn()), ERRORCODE.ERROR_407.value());
	}

	static Validation error408() {
		return holds(param -> !checkXMLEmpty(param.getXMLin()), ERRORCODE.ERROR_408.value());
	}

	static Validation error409() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_409.value());
	}

	static Validation error410() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_410.value());
	}

	static Validation error411() {
		return holds(param -> !checkOptionsForEncoding(param.getOptionsIn()), ERRORCODE.ERROR_411.value());
	}

	static Validation error412() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_412.value());
	}

	static Validation error413() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_413.value());
	}

	// checks if the user have the delete rights
	static Validation error414() {
		return holds(param -> true, ERRORCODE.ERROR_414.value());
	}

	static Validation error415() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_415.value());
	}

	static Validation error416() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_416.value());
	}

	static Validation error417() {
		return holds(param -> !checkNotNullUOM(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_417.value());
	}

	static Validation error418() {
		return holds(param -> !checkUniqueUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_418.value());
	}

	static Validation error419() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_419.value());
	}

	static Validation error420() {
		return holds(param -> !checkNodeValue(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_420.value());
	}

	// checks for return after delete call
	static Validation error421() {
		return holds(param -> true, ERRORCODE.ERROR_421.value());
	}

	// checks for GetBaseMessage input
	static Validation error422() {
		return holds(param -> true, ERRORCODE.ERROR_422.value());
	}

	// checks for GetCap
	static Validation error423() {
		return holds(param -> true, ERRORCODE.ERROR_423.value());
	}

	// checks for data version of OptionsIn for GetCap
	static Validation error424() {
		return holds(param -> !checkDataVerison(param.getCapabilitiesIn()), ERRORCODE.ERROR_424.value());
	}

	static Validation error425() {
		return holds(param -> !checkOptions(param.getWMLtypeIn(), param.getOptionsIn()), ERRORCODE.ERROR_425.value());
	}

	static Validation error429() {
		return holds(param -> !checkLogData(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_429.value());
	}

	static Validation error432() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_432.value());
	}

	static Validation error434() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_434.value());
	}

	static Validation error437() {
		return holds(param -> !checkMnemonicListUnique(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_437.value());
	}

	static Validation error438() {
		return holds(param -> checkMnemonicListUnique(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_438.value());
	}

	static Validation error439() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_439.value());
	}

	static Validation error443() {
		return holds(param -> !checkUOMWithUnitDirectory(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_443.value());
	}

	static Validation error445() {
		return holds(param -> !checkNodeValue(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_445.value());
	}

	static Validation error447() {
		return holds(param -> !checkUniqueUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_447.value());
	}

	static Validation error448() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_448.value());
	}

	static Validation error449() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_449.value());
	}

	static Validation error450() {
		return holds(param -> !checkMnemonicListUnique(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_450.value());
	}

	static Validation error453() {
		return holds(param -> !checkNotNullUOM(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_453.value());
	}

	static Validation error459() {
		return holds(param -> !checkMnemonicForSpecialCharacters(param.getXMLin()), ERRORCODE.ERROR_459.value());
	}

	static Validation error461() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_461.value());
	}

	static Validation error462() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_462.value());
	}

	static Validation error463() {
		return holds(param -> !checkUniqueUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_463.value());
	}

	static Validation error464() {
		return holds(param -> !checkUniqueUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_464.value());
	}

	static Validation error468() {
		return holds(param -> !checkSchemaVersion(param.getXMLin()), ERRORCODE.ERROR_468.value());
	}

	static Validation error475() {
		return holds(param -> !checkTrajForsubUID(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_475.value());
	}

	static Validation error482() {
		return holds(param -> !checkMnemonicListUnique(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_482.value());
	}

	static Validation error486() {
		return holds(param -> !checkIfXMLEqualsWMLObj(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_486.value());
	}

	static Validation error999() {
		// This error code is thrown if none of the custom error codes conditions are
		// met and this is the base error referring unknown base exception
		return holds(param -> false, ERRORCODE.ERROR_999.value());
	}

	static Validation holds(Predicate<ValidateParam> p, String message) {
		return param -> p.test(param) ? valid() : invalid(message);
	}

	static ValidationResult invalid(String message) {
		return new Invalid(Short.valueOf(message));

	}

	static ValidationResult valid() {
		return ValidationSupport.valid();
	}

	default Validation and(Validation other) {
		return user -> {
			final ValidationResult result = this.apply(user);
			return result.isValid() ? other.apply(user) : result;
		};
	}

	static boolean checkUOMUnit(String uomPattern, String uomUnit) {

		String regex = "(?<=^|,)" + uomPattern + "(?=,|$)";
		boolean result = Pattern.compile(regex).matcher(uomUnit).find();
		return result;
	}

	static boolean checkUOMWithUnitDirectory(String XMLin, String WMLTypein) {
		boolean result = false;
		List<AbstractWitsmlObject> witsmlObjects;
		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			witsmlObjects = WitsmlObjectParser.parse(WMLTypein, XMLin, version);
			switch (WMLTypein) {
			case "log":
				result = checkUOMWithUnitDirectoryLog(witsmlObjects);
				break;
			case "trajectory":
				result = checkUOMWithUnitDirectoryTrajectory(witsmlObjects);
				break;
			case "well":
				result = checkUOMWithUnitDirectoryWell(witsmlObjects);
				break;
			case "wellbore":
				result = checkUOMWithUnitDirectoryWellBore(witsmlObjects);
				break;
			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is " + e.getMessage());
		}
		return result;
	}

	static boolean checkUOMWithUnitDirectoryWell(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWell) {
				LOG.info("checking well object ");
				ObjWell objWell1311 = (ObjWell) abstractWitsmlObject;

				DimensionlessMeasure pcInterest = (DimensionlessMeasure) objWell1311.getPcInterest();

				if (checkUOMUnit(pcInterest.getUom(), uom1311units) == false) {

					result = true;
					break;
				}

				WellElevationCoord wellHeadElevation = (WellElevationCoord) objWell1311.getWellheadElevation();

				if (checkUOMUnit(wellHeadElevation.getUom().toString().toLowerCase(), uom1311units) == false) {

					result = true;
					break;
				}

				WellElevationCoord groundElevation = (WellElevationCoord) objWell1311.getGroundElevation();

				if (checkUOMUnit(groundElevation.getUom().toString().toLowerCase(), uom1311units) == false) {

					result = true;
					break;
				}

				WellVerticalDepthCoord waterDepth = (WellVerticalDepthCoord) objWell1311.getWaterDepth();

				if (checkUOMUnit(waterDepth.getUom().toString().toLowerCase(), uom1311units) == false) {
					result = true;
					break;
				}

				CsWellDatum wellDatum = (CsWellDatum) objWell1311.getWellDatum();
				if (checkUOMUnit(wellDatum.getElevation().getUom().toString().toLowerCase(), uom1311units) == false) {
					result = true;
					break;
				}

				CsLocation wellLocation = (CsLocation) objWell1311.getWellLocation();
				if (checkUOMUnit(wellLocation.getEasting().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellLocation.getNorthing().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				CsWellCRS wellCRS = (CsWellCRS) objWell1311.getWellCRS();
				if (checkUOMUnit(wellCRS.getGeographic().getXTranslation().getUom().toString(),
						uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getYTranslation().getUom().toString(),
						uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getZTranslation().getUom().toString(),
						uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getXRotation().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getYRotation().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getZRotation().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getEllipsoidSemiMajorAxis().getUom().toString(),
						uom1311units) == false) {
					result = true;
					break;
				}

				CsReferencePoint refrencePoint = (CsReferencePoint) objWell1311.getReferencePoint();
				if (checkUOMUnit(refrencePoint.getElevation().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(refrencePoint.getMeasuredDepth().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				CsLocation location = (CsLocation) refrencePoint.getLocation();
				if (checkUOMUnit(location.getEasting().getUom(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(location.getNorthing().getUom(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(location.getLatitude().getUom(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(location.getLongitude().getUom(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(location.getLocalX().getUom(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(location.getLocalY().getUom(), uom1311units) == false) {
					result = true;
					break;
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) {

				com.hashmapinc.tempus.WitsmlObjects.v1411.DimensionlessMeasure pcInterest = (com.hashmapinc.tempus.WitsmlObjects.v1411.DimensionlessMeasure) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getPcInterest();

				if (checkUOMUnit(pcInterest.getUom(), uom1411units) == false) {
					LOG.info("pcInterest");
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord wellHeadElevation = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellheadElevation();

				if (checkUOMUnit(wellHeadElevation.getUom().toString().toLowerCase(), uom1411units) == false) {
					LOG.info("wellHeadElevation");
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord groundElevation = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getGroundElevation();

				if (checkUOMUnit(groundElevation.getUom().toString().toLowerCase(), uom1411units) == false) {
					LOG.info("groundElevation");
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure waterDepth = (com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWaterDepth();

				if (checkUOMUnit(waterDepth.getUom().toString().toLowerCase(), uom1411units) == false) {
					LOG.info("waterDepth");
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum wellDatum = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellDatum();
				if (checkUOMUnit(wellDatum.getElevation().getUom().toString().toLowerCase(), uom1311units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation wellLocation = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellLocation();
				if (checkUOMUnit(wellLocation.getEasting().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellLocation.getNorthing().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS wellCRS = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellCRS();
				if (checkUOMUnit(wellCRS.getGeographic().getXTranslation().getUom().toString(),
						uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getYTranslation().getUom().toString(),
						uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getZTranslation().getUom().toString(),
						uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getXRotation().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getYRotation().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getZRotation().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(wellCRS.getGeographic().getEllipsoidSemiMajorAxis().getUom().toString(),
						uom1311units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint refrencePoint = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getReferencePoint();
				if (checkUOMUnit(refrencePoint.getElevation().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(refrencePoint.getMeasuredDepth().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation location = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation) refrencePoint
						.getLocation();
				if (checkUOMUnit(location.getEasting().getUom(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(location.getNorthing().getUom(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(location.getLatitude().getUom(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(location.getLongitude().getUom(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(location.getLocalX().getUom(), uom1311units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(location.getLocalY().getUom(), uom1311units) == false) {
					result = true;
					break;
				}

			}
		}

		return result;
	}

	static boolean checkUOMWithUnitDirectoryWellBore(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWellbore) {
				LOG.info("checking wellBore object ");
				ObjWellbore objWellbore1311 = (ObjWellbore) abstractWitsmlObject;

				MeasuredDepthCoord mdCurrent = (MeasuredDepthCoord) objWellbore1311.getMdCurrent();

				if (checkUOMUnit(mdCurrent.getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				WellVerticalDepthCoord tvdCurrent = (WellVerticalDepthCoord) objWellbore1311.getTvdCurrent();

				if (checkUOMUnit(tvdCurrent.getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				MeasuredDepthCoord mdKickoff = (MeasuredDepthCoord) objWellbore1311.getMdKickoff();

				if (checkUOMUnit(mdKickoff.getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				WellVerticalDepthCoord tvdKickoff = (WellVerticalDepthCoord) objWellbore1311.getTvdKickoff();

				if (checkUOMUnit(tvdKickoff.getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				MeasuredDepthCoord mdPlanned = (MeasuredDepthCoord) objWellbore1311.getMdPlanned();

				if (checkUOMUnit(mdPlanned.getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				WellVerticalDepthCoord vdPlanned = (WellVerticalDepthCoord) objWellbore1311.getTvdPlanned();

				if (checkUOMUnit(vdPlanned.getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				MeasuredDepthCoord seaPlanned = (MeasuredDepthCoord) objWellbore1311.getMdSubSeaPlanned();

				if (checkUOMUnit(seaPlanned.getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				WellVerticalDepthCoord tvSeaPlanned = (WellVerticalDepthCoord) objWellbore1311.getTvdSubSeaPlanned();

				if (checkUOMUnit(tvSeaPlanned.getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				TimeMeasure dayTarget = (TimeMeasure) objWellbore1311.getDayTarget();

				if (checkUOMUnit(dayTarget.getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) {

				com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord md = (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getMd();

				if (checkUOMUnit(md.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvd = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getTvd();

				if (checkUOMUnit(tvd.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord mbBit = (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getMdBit();

				if (checkUOMUnit(mbBit.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvdBit = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getTvdBit();

				if (checkUOMUnit(tvdBit.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord mdKickoff = (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getMdKickoff();

				if (checkUOMUnit(mdKickoff.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvdKickoff = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getTvdKickoff();

				if (checkUOMUnit(tvdKickoff.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord mdPlanned = (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getMdPlanned();

				if (checkUOMUnit(mdPlanned.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvdPlanned = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getTvdPlanned();

				if (checkUOMUnit(tvdPlanned.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord mdSubSeaPlanned = (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getMdSubSeaPlanned();

				if (checkUOMUnit(mdSubSeaPlanned.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvdSubSeaPlanned = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getTvdSubSeaPlanned();

				if (checkUOMUnit(tvdSubSeaPlanned.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.TimeMeasure dayTarget = (com.hashmapinc.tempus.WitsmlObjects.v1411.TimeMeasure) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getDayTarget();

				if (checkUOMUnit(dayTarget.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

			}
		}

		return result;
	}

	static boolean checkUOMWithUnitDirectoryLog(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjLog) {
				LOG.info("checking log object");
				ObjLog objLog1311 = (ObjLog) abstractWitsmlObject;

				if (checkUOMUnit(objLog1311.getStartIndex().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				if (checkUOMUnit(objLog1311.getEndIndex().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				if (checkUOMUnit(objLog1311.getStepIncrement().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				List<CsLogCurveInfo> logCurveInfo = objLog1311.getLogCurveInfo();
				for (CsLogCurveInfo curveInfo : logCurveInfo) {

					if (checkUOMUnit(curveInfo.getMinIndex().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(curveInfo.getMaxIndex().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (curveInfo.getSensorOffset().getUom() == null || curveInfo.getSensorOffset().getUom() != null
							&& curveInfo.getSensorOffset().getUom().isEmpty()) {
						result = true;
						break;
					}
					if (checkUOMUnit(curveInfo.getSensorOffset().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) {

				if (checkUOMUnit(((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
						.getStartIndex().getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				if (checkUOMUnit(((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject).getEndIndex()
						.getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
						.getStepIncrement().getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> logCurveInfo = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
						.getLogCurveInfo();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo curveInfo : logCurveInfo) {

					if (checkUOMUnit(curveInfo.getMinIndex().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(curveInfo.getMaxIndex().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(curveInfo.getSensorOffset().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
				}
			}
		}

		return result;
	}

	static boolean checkUOMWithUnitDirectoryTrajectory(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjTrajectory) {
				LOG.info("checking Trajectory object");
				ObjTrajectory objTraj1311 = (ObjTrajectory) abstractWitsmlObject;

				if (checkUOMUnit(objTraj1311.getMdMn().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				if (checkUOMUnit(objTraj1311.getMdMx().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				if (checkUOMUnit(objTraj1311.getMagDeclUsed().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				if (checkUOMUnit(objTraj1311.getGridCorUsed().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				if (checkUOMUnit(objTraj1311.getAziVertSect().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				if (checkUOMUnit(objTraj1311.getDispNsVertSectOrig().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}

				if (checkUOMUnit(objTraj1311.getDispEwVertSectOrig().getUom().toString(), uom1311units) == false) {
					result = true;
					break;
				}
				List<CsTrajectoryStation> trajectoryStation = objTraj1311.getTrajectoryStation();
				for (CsTrajectoryStation trajStation : trajectoryStation) {

					if (checkUOMUnit(trajStation.getMd().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getTvd().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getIncl().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getAzi().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getMtf().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getGtf().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getDispEw().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getDispNs().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getVertSect().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getDls().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getRateTurn().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getRateBuild().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getMdDelta().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getTvdDelta().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getGravTotalUncert().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getDipAngleUncert().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getMagTotalUncert().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					CsStnTrajRawData trajRawData = (CsStnTrajRawData) trajStation.getRawData();

					if (checkUOMUnit(trajRawData.getGravAxialRaw().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajRawData.getGravTran1Raw().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajRawData.getGravTran2Raw().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajRawData.getMagAxialRaw().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(trajRawData.getMagTran1Raw().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(trajRawData.getMagTran2Raw().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					CsStnTrajCorUsed crUSed = (CsStnTrajCorUsed) trajStation.getCorUsed();

					if (checkUOMUnit(crUSed.getGravAxialAccelCor().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getGravTran1AccelCor().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getGravTran2AccelCor().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getMagAxialDrlstrCor().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getMagTran1DrlstrCor().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getMagTran2DrlstrCor().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getSagAziCor().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getSagIncCor().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getStnGridCorUsed().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getStnMagDeclUsed().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getDirSensorOffset().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					CsStnTrajValid stnValid = (CsStnTrajValid) trajStation.getValid();

					if (checkUOMUnit(stnValid.getMagTotalFieldCalc().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(stnValid.getMagDipAngleCalc().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(stnValid.getGravTotalFieldCalc().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					CsStnTrajMatrixCov matric = (CsStnTrajMatrixCov) trajStation.getMatrixCov();

					if (checkUOMUnit(matric.getVarianceEE().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(matric.getVarianceNN().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(matric.getVarianceEVert().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(matric.getVarianceNVert().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(matric.getVarianceVertVert().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(matric.getBiasE().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(matric.getBiasN().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(matric.getBiasVert().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					List<CsLocation> location = trajStation.getLocation();
					for (CsLocation loc : location) {

						if (checkUOMUnit(loc.getLatitude().getUom().toString(), uom1311units) == false) {
							result = true;
							break;
						}
						if (checkUOMUnit(loc.getLongitude().getUom().toString(), uom1311units) == false) {
							result = true;
							break;
						}
						if (checkUOMUnit(loc.getEasting().getUom().toString(), uom1311units) == false) {
							result = true;
							break;
						}
						if (checkUOMUnit(loc.getNorthing().getUom().toString(), uom1311units) == false) {
							result = true;
							break;
						}
						if (checkUOMUnit(loc.getLocalX().getUom().toString(), uom1311units) == false) {
							result = true;
							break;
						}
						if (checkUOMUnit(loc.getLocalY().getUom().toString(), uom1311units) == false) {
							result = true;
							break;
						}

					}

				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) {

				if (checkUOMUnit(((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getMdMn().getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getMdMx().getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getMagDeclUsed().getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getGridCorUsed().getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getAziVertSect().getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getDispNsVertSectOrig().getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}
				if (checkUOMUnit(((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getDispEwVertSectOrig().getUom().toString(), uom1411units) == false) {
					result = true;
					break;
				}

				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsTrajectoryStation> trajectoryStation = (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getTrajectoryStation());
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsTrajectoryStation trajStation : trajectoryStation) {
					if (checkUOMUnit(trajStation.getMd().getUom().toString(), uom1311units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getTvd().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getIncl().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getAzi().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getMtf().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getGtf().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getDispEw().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getDispNs().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getVertSect().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getDls().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getRateTurn().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getRateBuild().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getMdDelta().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getTvdDelta().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getGravTotalUncert().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getDipAngleUncert().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajStation.getMagTotalUncert().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajRawData trajRawData = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajRawData) trajStation
							.getRawData();

					if (checkUOMUnit(trajRawData.getGravAxialRaw().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajRawData.getGravTran1Raw().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajRawData.getGravTran2Raw().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(trajRawData.getMagAxialRaw().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(trajRawData.getMagTran1Raw().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(trajRawData.getMagTran2Raw().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajCorUsed crUSed = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajCorUsed) trajStation
							.getCorUsed();

					if (checkUOMUnit(crUSed.getGravAxialAccelCor().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getGravTran1AccelCor().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getGravTran2AccelCor().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getMagAxialDrlstrCor().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getMagTran1DrlstrCor().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getMagTran2DrlstrCor().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getSagAziCor().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getSagIncCor().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getStnGridCorUsed().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getStnMagDeclUsed().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(crUSed.getDirSensorOffset().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajValid stnValid = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajValid) trajStation
							.getValid();

					if (checkUOMUnit(stnValid.getMagTotalFieldCalc().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(stnValid.getMagDipAngleCalc().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(stnValid.getGravTotalFieldCalc().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajMatrixCov matric = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajMatrixCov) trajStation
							.getMatrixCov();

					if (checkUOMUnit(matric.getVarianceEE().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(matric.getVarianceNN().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(matric.getVarianceEVert().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(matric.getVarianceNVert().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(matric.getVarianceVertVert().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}
					if (checkUOMUnit(matric.getBiasE().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(matric.getBiasN().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					if (checkUOMUnit(matric.getBiasVert().getUom().toString(), uom1411units) == false) {
						result = true;
						break;
					}

					List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation> location = trajStation.getLocation();
					for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation loc : location) {
						if (checkUOMUnit(loc.getLatitude().getUom().toString(), uom1311units) == false) {
							result = true;
							break;
						}
						if (checkUOMUnit(loc.getLongitude().getUom().toString(), uom1411units) == false) {
							result = true;
							break;
						}
						if (checkUOMUnit(loc.getEasting().getUom().toString(), uom1411units) == false) {
							result = true;
							break;
						}
						if (checkUOMUnit(loc.getNorthing().getUom().toString(), uom1411units) == false) {
							result = true;
							break;
						}
						if (checkUOMUnit(loc.getLocalX().getUom().toString(), uom1411units) == false) {
							result = true;
							break;
						}
						if (checkUOMUnit(loc.getLocalY().getUom().toString(), uom1411units) == false) {
							result = true;
							break;
						}
					}

				}
			}
		}

		return result;
	}

	/**
	 * This method validates the XMLin against the schemaLocation.
	 * 
	 * @param xmlFileUrl
	 * @param schemaFileUrl
	 * @return true if the XMLin is validated
	 */
	static boolean schemaValidate(String xmlFileUrl, String schemaFileUrl) {
		Objects.requireNonNull(xmlFileUrl);
		Objects.requireNonNull(schemaFileUrl);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(new URL(schemaFileUrl));

			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(xmlFileUrl));
			return true;
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method checks for empty WMLType
	 * 
	 * @param WMLType
	 * @return true if empty else false
	 */
	static boolean checkWMLTypeEmpty(String WMLType) {
		boolean result = false;
		LOG.info("The WMLType in is : " + WMLType);
		if (WMLType.trim().isEmpty()) {
			result = true;
		}
		return result;
	}

	/**
	 * This method checks for empty WMLType
	 * 
	 * @param WMLType
	 * @return true if empty else false
	 */
	static boolean checkDataVerison(String OptionsIn) {
		boolean result = false;
		if (OptionsIn.trim().isEmpty()) {
			result = true;
		}
		return result;
	}

	/**
	 * This method checks for XML empty
	 * 
	 * @param XMLin
	 * @return true if empty else false
	 */
	static boolean checkXMLEmpty(String XMLin) {
		boolean result = false;
		if (XMLin.trim().isEmpty()) {
			result = true;
		}
		return result;
	}

	/**
	 * This method checks for XML empty
	 * 
	 * @param XMLin
	 * @return true if empty else false
	 */
	static boolean checkCapabilitiesEmpty(String Capabilities) {
		boolean result = false;
		if (Capabilities.trim().isEmpty()) {
			result = true;
		}
		return result;
	}

	/**
	 * This method check if XML Object equals WML Object
	 * 
	 * @param XMLin
	 * @param WMLType
	 * @return true if equal else false
	 */
	static boolean checkIfXMLEqualsWMLObj(String XMLin, String WMLType) {
		boolean result = false;

		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			switch (WMLType) {
			case "log":
				if (!XMLin.contains("log")) {
					result = true;
				}
				break;

			case "trajectory":
				if (!XMLin.contains("trajectory")) {
					result = true;
				}
				break;

			case "well":

				if (!XMLin.contains("well")) {
					result = true;
				}
				break;

			case "wellbore":
				if (!XMLin.contains("wellbore")) {
					result = true;
				}
				break;

			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLType);
			}
		} catch (Exception e) {
			LOG.warning("the error is " + e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks for multiple well tag in XMLin
	 * 
	 * @param XMLin
	 * @return true if exists else false
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkWell(String XMLin, String WMLTypein) {
		boolean result = false;

		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			switch (WMLTypein) {
			case "log":
				if (!XMLin.contains("logs")) {
					result = true;
				}
				break;

			case "trajectory":
				if (!XMLin.contains("trajectorys")) {
					result = true;
				}
				break;

			case "well":
				LOG.info("checking for wells:" + XMLin.contains("wells"));
				if (!XMLin.contains("wells")) {
					result = true;
				}
				break;

			case "wellbore":
				if (!XMLin.contains("wellbores")) {
					result = true;
				}
				break;

			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is " + e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks checks for the XMl version as supported by the server.
	 * 
	 * @param XMLin
	 * @return true if they do not match.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkSchemaVersion(String XMLin) {
		boolean result = false;
		try {
			String version = WitsmlUtil.getVersionFromXML(XMLin);
			if (version != "1.3.1.1" || version != "1.4.1.1") {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}

		return result;
	}

	/**
	 * This method checks NameSpace for XMLin
	 * 
	 * @param XMLin
	 * @return true if check fails
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkNameSpace(String XMLin) {
		boolean result = false;
		try {
			if (!XMLin.contains("xmlns")) {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks for logData tag in XMLin
	 * 
	 * @param XMLin
	 * @return true if exists else false
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkLogData(String XMLin, String WMLTypein) {
		boolean result = false;

		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);

			switch (WMLTypein) {
			case "log":
				result = checkLogDataforLOG(XMLin);
				break;
			case "trajectory":
				result = false;
				break;
			case "well":
				result = false;
				break;
			case "wellbore":
				result = false;
				break;
			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is " + e.getMessage());
		}
		return result;
	}

	static boolean checkLogDataforLOG(String XMLin) {
		boolean result = false;
		try {
			if (XMLin.split(LOG_XML_TAG).length > 2) {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks for UID to be null.
	 * 
	 * @param XMLin
	 * @return true if uid is null else false
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkNotNullUid(String XMLin, String WMLTypein) {
		boolean result = false;
		List<AbstractWitsmlObject> witsmlObjects;
		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			witsmlObjects = WitsmlObjectParser.parse(WMLTypein, XMLin, version);
			switch (WMLTypein) {
			case "log":
				result = checkNotNullUidForDiffVersionLog(witsmlObjects);
				break;
			case "trajectory":
				result = checkNotNullUidForDiffVersionTrajectory(witsmlObjects);
				break;
			case "well":
				result = checkNotNullUidForDiffVersionWell(witsmlObjects);
				break;
			case "wellbore":
				result = checkNotNullUidForDiffVersionWellbore(witsmlObjects);
				break;
			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is " + e.getMessage());
		}
		return result;
	}

	static boolean checkNotNullUidForDiffVersionLog(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjLog) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}
				List<CsLogCurveInfo> logCurveInfos = ((ObjLog) abstractWitsmlObject).getLogCurveInfo();
				for (CsLogCurveInfo csLogCurveInfo : logCurveInfos) {
					if (csLogCurveInfo.getUid() == null
							|| (csLogCurveInfo.getUid() != null && csLogCurveInfo.getUid().isEmpty())) {
						result = true;
						break;
					}
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> logCurveInfos = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
						.getLogCurveInfo();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo csLogCurveInfo : logCurveInfos) {
					if (csLogCurveInfo.getUid() == null
							|| (csLogCurveInfo.getUid() != null && csLogCurveInfo.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
			}
		}

		return result;
	}

	static boolean checkNotNullUidForDiffVersionTrajectory(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjTrajectory) {

				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}

			}
		}

		return result;
	}

	static boolean checkNotNullUidForDiffVersionWellbore(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWellbore) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}

			}
		}

		return result;
	}

	static boolean checkNotNullUidForDiffVersionWell(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWell) {
				LOG.info("checking well object ");
				ObjWell objWell1311 = (ObjWell) abstractWitsmlObject;
				if (objWell1311.getUid() == null || (objWell1311.getUid() != null && objWell1311.getUid().isEmpty())) {
					result = true;
					break;
				}
				List<CsReferencePoint> wellRefrenceinfo = objWell1311.getReferencePoint();
				for (CsReferencePoint refrencePoint : wellRefrenceinfo) {
					if (refrencePoint.getUid() == null
							|| (refrencePoint.getUid() != null && refrencePoint.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
				List<CsWellDatum> wellDatum = objWell1311.getWellDatum();
				for (CsWellDatum datum : wellDatum) {
					if (datum.getUid() == null || (datum.getUid() != null && datum.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
				List<CsLocation> wellLocation = objWell1311.getWellLocation();
				for (CsLocation location : wellLocation) {
					if (location.getUid() == null || (location.getUid() != null && location.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
				List<CsWellCRS> wellCRS = objWell1311.getWellCRS();
				for (CsWellCRS crs : wellCRS) {
					if (crs.getUid() == null || (crs.getUid() != null && crs.getUid().isEmpty())) {
						result = true;
						break;
					}
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint> wellRefrenceinfo = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getReferencePoint();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint refrencePoint : wellRefrenceinfo) {
					if (refrencePoint.getUid() == null
							|| (refrencePoint.getUid() != null && refrencePoint.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum> wellDatum = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellDatum();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum datum : wellDatum) {
					if (datum.getUid() == null || (datum.getUid() != null && datum.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation> wellLocation = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellLocation();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation location : wellLocation) {
					if (location.getUid() == null || (location.getUid() != null && location.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS> wellCRS = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellCRS();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS crs : wellCRS) {
					if (crs.getUid() == null || (crs.getUid() != null && crs.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
			}
		}

		return result;
	}

	/**
	 * This method checks for the Node Value to be empty or blank.
	 * 
	 * @param XMLin
	 * @return true is empty node value is found.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkNodeValue(String XMLin, String WMLTypein) {
		boolean result = false;
		List<AbstractWitsmlObject> witsmlObjects;
		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version for Node Value is: " + version);
			witsmlObjects = WitsmlObjectParser.parse(WMLTypein, XMLin, version);
			switch (WMLTypein) {
			case "log":
				result = checkNotNullUidForLog(witsmlObjects);
				break;
			case "trajectory":
				result = checkNotNullNodeTrjaectory(witsmlObjects);
				break;
			case "well":
				LOG.info("checking node Value for well");
				result = checkNotNullNodeWell(witsmlObjects);
				break;
			case "wellbore":
				result = checkNotNullNodeWellbore(witsmlObjects);
				break;
			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is the " + e.getMessage());
		}
		return result;
	}

	static boolean checkNotNullNodeWell(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWell) {
				LOG.info("checking well object for Not Null Well");
				ObjWell objWell1311 = (ObjWell) abstractWitsmlObject;
				if (objWell1311.getName() == null
						|| (objWell1311.getName() != null && objWell1311.getName().isEmpty())) {

					result = true;
					break;
				} else if (objWell1311.getNameLegal() == null
						|| (objWell1311.getNameLegal() != null && objWell1311.getNameLegal().isEmpty())) {
					LOG.info("NameLegal");
					result = true;
					break;
				} else if (objWell1311.getNumLicense() == null
						|| (objWell1311.getNumLicense() != null && objWell1311.getNumLicense().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getNumGovt() == null
						|| (objWell1311.getNumGovt() != null && objWell1311.getNumGovt().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getField() == null
						|| (objWell1311.getField() != null && objWell1311.getField().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getCountry() == null
						|| (objWell1311.getCountry() != null && objWell1311.getCountry().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getRegion() == null
						|| (objWell1311.getRegion() != null && objWell1311.getRegion().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getDistrict() == null
						|| (objWell1311.getDistrict() != null && objWell1311.getDistrict().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getBlock() == null
						|| (objWell1311.getBlock() != null && objWell1311.getBlock().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getTimeZone() == null
						|| (objWell1311.getTimeZone() != null && objWell1311.getTimeZone().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getOperator() == null
						|| (objWell1311.getOperator() != null && objWell1311.getOperator().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getOperatorDiv() == null
						|| (objWell1311.getOperatorDiv() != null && objWell1311.getOperatorDiv().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getPcInterest() == null
						|| (objWell1311.getPcInterest() != null && objWell1311.getPcInterest().toString().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getNumAPI() == null
						|| (objWell1311.getNumAPI() != null && objWell1311.getNumAPI().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getStatusWell() == null
						|| (objWell1311.getStatusWell() != null && objWell1311.getStatusWell().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getPurposeWell() == null
						|| (objWell1311.getPurposeWell() != null && objWell1311.getPurposeWell().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getDTimSpud() == null
						|| (objWell1311.getDTimSpud() != null && objWell1311.getDTimSpud().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getDTimPa() == null
						|| (objWell1311.getDTimPa() != null && objWell1311.getDTimPa().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getWellheadElevation() == null || (objWell1311.getWellheadElevation() != null
						&& objWell1311.getWellheadElevation().toString().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getGroundElevation() == null || (objWell1311.getGroundElevation() != null
						&& objWell1311.getGroundElevation().toString().isEmpty())) {
					result = true;
					break;
				} else if (objWell1311.getWaterDepth() == null
						|| (objWell1311.getWaterDepth() != null && objWell1311.getWaterDepth().toString().isEmpty())) {
					result = true;
					break;
				}
				List<CsWellDatum> wellDatum = objWell1311.getWellDatum();
				for (CsWellDatum datum : wellDatum) {
					if (datum.getName() == null || (datum.getName() != null && datum.getName().isEmpty())) {
						result = true;
						break;
					}
					if (datum.getCode() == null || (datum.getCode() != null && datum.getCode().toString().isEmpty())) {
						result = true;
						break;
					}
					if (datum.getElevation() == null
							|| (datum.getElevation() != null && datum.getElevation().toString().isEmpty())) {
						result = true;
						break;
					}
				}
				List<CsLocation> wellLocation = objWell1311.getWellLocation();
				for (CsLocation location : wellLocation) {
					if (location.getWellCRS() == null
							|| (location.getWellCRS() != null && location.getWellCRS().toString().isEmpty())) {
						result = true;
						break;
					}
					if (location.getEasting() == null
							|| (location.getEasting() != null && location.getEasting().toString().isEmpty())) {
						result = true;
						break;
					}
					if (location.getNorthing() == null
							|| (location.getNorthing() != null && location.getNorthing().toString().isEmpty())) {
						result = true;
						break;
					}
					if (location.getDescription() == null
							|| (location.getDescription() != null && location.getDescription().isEmpty())) {
						result = true;
						break;
					}
				}
				List<CsReferencePoint> referencePoint = objWell1311.getReferencePoint();
				for (CsReferencePoint reference : referencePoint) {
					if (reference.getName() == null || (reference.getName() != null && reference.getName().isEmpty())) {
						result = true;
						break;
					}
					if (reference.getType() == null || (reference.getType() != null && reference.getType().isEmpty())) {
						result = true;
						break;
					}
					if (reference.getElevation() == null
							|| (reference.getElevation() != null && reference.getElevation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (reference.getMeasuredDepth() == null || (reference.getMeasuredDepth() != null
							&& reference.getMeasuredDepth().toString().isEmpty())) {
						result = true;
						break;
					}
					List<CsLocation> location = objWell1311.getWellLocation();
					for (CsLocation loc : location) {
						if (loc.getWellCRS() == null
								|| (loc.getWellCRS() != null && loc.getWellCRS().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getEasting() == null
								|| (loc.getEasting() != null && loc.getEasting().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getNorthing() == null
								|| (loc.getNorthing() != null && loc.getNorthing().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getLocalX() == null
								|| (loc.getLocalX() != null && loc.getLocalX().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getLocalY() == null
								|| (loc.getLocalY() != null && loc.getLocalY().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getDescription() == null
								|| (loc.getDescription() != null && loc.getDescription().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getLongitude() == null
								|| (loc.getLongitude() != null && loc.getLongitude().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getLatitude() == null
								|| (loc.getLatitude() != null && loc.getLatitude().toString().isEmpty())) {
							result = true;
							break;
						}
					}

				}
				List<CsWellCRS> wellCRS = objWell1311.getWellCRS();
				for (CsWellCRS crs : wellCRS) {
					if (crs.getName() == null || (crs.getName() != null && crs.getName().isEmpty())) {
						result = true;
						break;
					}
					CsGeodeticModel geo = (CsGeodeticModel) crs.getGeographic();

					if (geo.getNameCRS() == null
							|| (geo.getNameCRS() != null && geo.getNameCRS().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getGeodeticDatumCode() == null || (geo.getGeodeticDatumCode() != null
							&& geo.getGeodeticDatumCode().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getXTranslation() == null
							|| (geo.getXTranslation() != null && geo.getXTranslation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getYTranslation() == null
							|| (geo.getYTranslation() != null && geo.getYTranslation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getZTranslation() == null
							|| (geo.getZTranslation() != null && geo.getZTranslation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getXRotation() == null
							|| (geo.getXRotation() != null && geo.getXRotation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getYRotation() == null
							|| (geo.getYRotation() != null && geo.getYRotation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getZRotation() == null
							|| (geo.getZRotation() != null && geo.getZRotation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getScaleFactor() == null
							|| (geo.getScaleFactor() != null && geo.getScaleFactor().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getEllipsoidCode() == null
							|| (geo.getEllipsoidCode() != null && geo.getEllipsoidCode().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getEllipsoidSemiMajorAxis() == null || (geo.getEllipsoidSemiMajorAxis() != null
							&& geo.getEllipsoidSemiMajorAxis().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getEllipsoidInverseFlattening() == null || (geo.getEllipsoidInverseFlattening() != null
							&& geo.getEllipsoidInverseFlattening().toString().isEmpty())) {
						result = true;
						break;
					}

					CsProjectionx projection = (CsProjectionx) crs.getMapProjection();

					if (projection.getNameCRS() == null
							|| projection.getNameCRS() != null && projection.getNameCRS().toString().isEmpty()) {
						result = true;
						break;
					}
					if (projection.getProjectionCode() == null || projection.getProjectionCode() != null
							&& projection.getProjectionCode().toString().isEmpty()) {
						result = true;
						break;
					}
					if (projection.getProjectedFrom() == null || projection.getProjectedFrom() != null
							&& projection.getProjectedFrom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (projection.getZone() == null
							|| projection.getZone() != null && projection.getZone().toString().isEmpty()) {
						result = true;
						break;
					}

					CsLocalCRS local = (CsLocalCRS) crs.getLocalCRS();

					if (local.getYAxisAzimuth() == null
							|| local.getYAxisAzimuth() != null && local.getYAxisAzimuth().toString().isEmpty()) {
						result = true;
						break;
					}
					if (local.getYAxisDescription() == null || local.getYAxisDescription() != null
							&& local.getYAxisDescription().toString().isEmpty()) {
						result = true;
						break;
					}
					if (local.isUsesWellAsOrigin() == null
							|| local.isUsesWellAsOrigin() != null && local.isUsesWellAsOrigin().toString().isEmpty()) {
						result = true;
						break;
					}
					if (local.isXRotationCounterClockwise() == null || local.isXRotationCounterClockwise() != null
							&& local.isXRotationCounterClockwise().toString().isEmpty()) {
						result = true;
						break;
					}
				}

				CsCommonData commonData = (CsCommonData) objWell1311.getCommonData();

				if (commonData.getDTimCreation() == null
						|| commonData.getDTimCreation() != null && commonData.getDTimCreation().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getDTimLastChange() == null || commonData.getDTimLastChange() != null
						&& commonData.getDTimLastChange().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getItemState() == null
						|| commonData.getItemState() != null && commonData.getItemState().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getComments() == null
						|| commonData.getComments() != null && commonData.getComments().isEmpty()) {
					result = true;
					break;
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}
				if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getUid() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getUid() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getUid()
										.isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getName() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getName() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getName()
										.isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getNameLegal() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getNameLegal() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getNameLegal().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getNumLicense() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getNumLicense() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getNumLicense().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getNumGovt() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getNumGovt() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getNumGovt().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getField() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getField() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getField()
										.isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getCountry() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getCountry() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getCountry().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getRegion() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getRegion() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getRegion().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getDistrict() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getDistrict() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getDistrict().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getBlock() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getBlock() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getBlock()
										.isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getTimeZone() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getTimeZone() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getTimeZone().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getOperator() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getOperator() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getOperator().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getOperatorDiv() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getOperatorDiv() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getOperatorDiv().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getPcInterest() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getPcInterest() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getPcInterest().toString().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getNumAPI() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getNumAPI() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getNumAPI().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getStatusWell() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getStatusWell() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getStatusWell().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getPurposeWell() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getPurposeWell() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getPurposeWell().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getDTimSpud() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getDTimSpud() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getDTimSpud().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getDTimPa() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getDTimPa() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getDTimPa().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellheadElevation() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getWellheadElevation() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getWellheadElevation().toString().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getGroundElevation() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getGroundElevation() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getGroundElevation().toString().isEmpty())) {
					result = true;
					break;
				} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWaterDepth() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
								.getWaterDepth() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
										.getWaterDepth().toString().isEmpty())) {
					result = true;
					break;
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum> wellDatum = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellDatum();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum datum : wellDatum) {
					if (datum.getName() == null || (datum.getName() != null && datum.getName().isEmpty())) {
						result = true;
						break;
					}
					if (datum.getCode() == null || (datum.getCode() != null && datum.getCode().toString().isEmpty())) {
						result = true;
						break;
					}
					if (datum.getElevation() == null
							|| (datum.getElevation() != null && datum.getElevation().toString().isEmpty())) {
						result = true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation> wellLocation = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellLocation();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation location : wellLocation) {
					if (location.getWellCRS() == null
							|| (location.getWellCRS() != null && location.getWellCRS().toString().isEmpty())) {
						result = true;
						break;
					}
					if (location.getEasting() == null
							|| (location.getEasting() != null && location.getEasting().toString().isEmpty())) {
						result = true;
						break;
					}
					if (location.getNorthing() == null
							|| (location.getNorthing() != null && location.getNorthing().toString().isEmpty())) {
						result = true;
						break;
					}
					if (location.getDescription() == null
							|| (location.getDescription() != null && location.getDescription().isEmpty())) {
						result = true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint> referencePoint = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getReferencePoint();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint reference : referencePoint) {
					if (reference.getName() == null || (reference.getName() != null && reference.getName().isEmpty())) {
						result = true;
						break;
					}
					if (reference.getType() == null || (reference.getType() != null && reference.getType().isEmpty())) {
						result = true;
						break;
					}
					if (reference.getElevation() == null
							|| (reference.getElevation() != null && reference.getElevation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (reference.getMeasuredDepth() == null || (reference.getMeasuredDepth() != null
							&& reference.getMeasuredDepth().toString().isEmpty())) {
						result = true;
						break;
					}
					List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation> location = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
							.getWellLocation();
					for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation loc : location) {
						if (loc.getWellCRS() == null
								|| (loc.getWellCRS() != null && loc.getWellCRS().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getEasting() == null
								|| (loc.getEasting() != null && loc.getEasting().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getNorthing() == null
								|| (loc.getNorthing() != null && loc.getNorthing().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getLocalX() == null
								|| (loc.getLocalX() != null && loc.getLocalX().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getLocalY() == null
								|| (loc.getLocalY() != null && loc.getLocalY().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getDescription() == null
								|| (loc.getDescription() != null && loc.getDescription().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getLongitude() == null
								|| (loc.getLongitude() != null && loc.getLongitude().toString().isEmpty())) {
							result = true;
							break;
						}
						if (loc.getLatitude() == null
								|| (loc.getLatitude() != null && loc.getLatitude().toString().isEmpty())) {
							result = true;
							break;
						}
					}

				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS> wellCRS = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellCRS();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS crs : wellCRS) {
					if (crs.getName() == null || (crs.getName() != null && crs.getName().isEmpty())) {
						result = true;
						break;
					}
					com.hashmapinc.tempus.WitsmlObjects.v1411.CsGeodeticModel geo = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsGeodeticModel) crs
							.getGeographic();

					if (geo.getNameCRS() == null
							|| (geo.getNameCRS() != null && geo.getNameCRS().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getGeodeticDatumCode() == null || (geo.getGeodeticDatumCode() != null
							&& geo.getGeodeticDatumCode().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getXTranslation() == null
							|| (geo.getXTranslation() != null && geo.getXTranslation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getYTranslation() == null
							|| (geo.getYTranslation() != null && geo.getYTranslation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getZTranslation() == null
							|| (geo.getZTranslation() != null && geo.getZTranslation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getXRotation() == null
							|| (geo.getXRotation() != null && geo.getXRotation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getYRotation() == null
							|| (geo.getYRotation() != null && geo.getYRotation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getZRotation() == null
							|| (geo.getZRotation() != null && geo.getZRotation().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getScaleFactor() == null
							|| (geo.getScaleFactor() != null && geo.getScaleFactor().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getEllipsoidCode() == null
							|| (geo.getEllipsoidCode() != null && geo.getEllipsoidCode().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getEllipsoidSemiMajorAxis() == null || (geo.getEllipsoidSemiMajorAxis() != null
							&& geo.getEllipsoidSemiMajorAxis().toString().isEmpty())) {
						result = true;
						break;
					}
					if (geo.getEllipsoidInverseFlattening() == null || (geo.getEllipsoidInverseFlattening() != null
							&& geo.getEllipsoidInverseFlattening().toString().isEmpty())) {
						result = true;
						break;
					}

					com.hashmapinc.tempus.WitsmlObjects.v1411.CsProjectionx mapProjection = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsProjectionx) crs
							.getMapProjection();

					if (mapProjection.getNameCRS() == null
							|| mapProjection.getNameCRS() != null && mapProjection.getNameCRS().toString().isEmpty()) {
						result = true;
						break;
					}
					if (mapProjection.getProjectionCode() == null || mapProjection.getProjectionCode() != null
							&& mapProjection.getProjectionCode().toString().isEmpty()) {
						result = true;
						break;
					}
					if (mapProjection.getProjectedFrom() == null || mapProjection.getProjectedFrom() != null
							&& mapProjection.getProjectedFrom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (mapProjection.getZone() == null
							|| mapProjection.getZone() != null && mapProjection.getZone().toString().isEmpty()) {
						result = true;
						break;
					}

					com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocalCRS localCRS = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocalCRS) crs
							.getLocalCRS();

					if (localCRS.getYAxisAzimuth() == null
							|| localCRS.getYAxisAzimuth() != null && localCRS.getYAxisAzimuth().toString().isEmpty()) {
						result = true;
						break;
					}
					if (localCRS.getYAxisDescription() == null || localCRS.getYAxisDescription() != null
							&& localCRS.getYAxisDescription().toString().isEmpty()) {
						result = true;
						break;
					}
					if (localCRS.isUsesWellAsOrigin() == null || localCRS.isUsesWellAsOrigin() != null
							&& localCRS.isUsesWellAsOrigin().toString().isEmpty()) {
						result = true;
						break;
					}
					if (localCRS.isXRotationCounterClockwise() == null || localCRS.isXRotationCounterClockwise() != null
							&& localCRS.isXRotationCounterClockwise().toString().isEmpty()) {
						result = true;
						break;
					}

				}
				com.hashmapinc.tempus.WitsmlObjects.v1411.CsCommonData commonData = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsCommonData) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getCommonData();

				if (commonData.getDTimCreation() == null
						|| commonData.getDTimCreation() != null && commonData.getDTimCreation().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getDTimLastChange() == null || commonData.getDTimLastChange() != null
						&& commonData.getDTimLastChange().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getItemState() == null
						|| commonData.getItemState() != null && commonData.getItemState().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getComments() == null
						|| commonData.getComments() != null && commonData.getComments().isEmpty()) {
					result = true;
					break;
				}

			}
		}

		return result;
	}

	static boolean checkNotNullNodeWellbore(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWellbore) {
				LOG.info("checking wellBore object ");
				ObjWellbore objWellbore1311 = (ObjWellbore) abstractWitsmlObject;

				if (objWellbore1311.getName() == null
						|| objWellbore1311.getName() != null && objWellbore1311.getName().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getNameWell() == null
						|| objWellbore1311.getNameWell() != null && objWellbore1311.getNameWell().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getNumber() == null
						|| objWellbore1311.getNumber() != null && objWellbore1311.getNumber().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getSuffixAPI() == null
						|| objWellbore1311.getSuffixAPI() != null && objWellbore1311.getSuffixAPI().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getNumGovt() == null
						|| objWellbore1311.getNumGovt() != null && objWellbore1311.getNumGovt().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getStatusWellbore() == null || objWellbore1311.getStatusWellbore() != null
						&& objWellbore1311.getStatusWellbore().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getPurposeWellbore() == null || objWellbore1311.getPurposeWellbore() != null
						&& objWellbore1311.getPurposeWellbore().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getTypeWellbore() == null
						|| objWellbore1311.getTypeWellbore() != null && objWellbore1311.getTypeWellbore().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getShape() == null
						|| objWellbore1311.getShape() != null && objWellbore1311.getShape().isEmpty()) {
					result = true;
					break;
				}
				try {
					if (objWellbore1311.getDTimKickoff() == null
							|| objWellbore1311.getDTimKickoff() != null && objWellbore1311.getDTimKickoff().isEmpty()) {
						result = true;
						break;
					}
				} catch (DatatypeConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (objWellbore1311.getMdCurrent() == null || objWellbore1311.getMdCurrent() != null
						&& objWellbore1311.getMdCurrent().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getTvdCurrent() == null || objWellbore1311.getTvdCurrent() != null
						&& objWellbore1311.getTvdCurrent().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getMdPlanned() == null || objWellbore1311.getMdPlanned() != null
						&& objWellbore1311.getMdPlanned().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getTvdPlanned() == null || objWellbore1311.getTvdPlanned() != null
						&& objWellbore1311.getTvdPlanned().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getMdSubSeaPlanned() == null || objWellbore1311.getMdSubSeaPlanned() != null
						&& objWellbore1311.getMdSubSeaPlanned().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getTvdSubSeaPlanned() == null || objWellbore1311.getTvdSubSeaPlanned() != null
						&& objWellbore1311.getTvdSubSeaPlanned().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1311.getDayTarget() == null || objWellbore1311.getDayTarget() != null
						&& objWellbore1311.getDayTarget().toString().isEmpty()) {
					result = true;
					break;
				}
				CsCommonData commonData = (CsCommonData) objWellbore1311.getCommonData();

				if (commonData.getDTimCreation() == null
						|| commonData.getDTimCreation() != null && commonData.getDTimCreation().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getDTimLastChange() == null || commonData.getDTimLastChange() != null
						&& commonData.getDTimLastChange().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getItemState() == null
						|| commonData.getItemState() != null && commonData.getItemState().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getComments() == null
						|| commonData.getComments() != null && commonData.getComments().toString().isEmpty()) {
					result = true;
					break;
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) {

				com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore objWellbore1411 = (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject;

				if (objWellbore1411.getName() == null
						|| objWellbore1411.getName() != null && objWellbore1411.getName().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getNameWell() == null
						|| objWellbore1411.getNameWell() != null && objWellbore1411.getNameWell().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getNumber() == null
						|| objWellbore1411.getNumber() != null && objWellbore1411.getNumber().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getSuffixAPI() == null
						|| objWellbore1411.getSuffixAPI() != null && objWellbore1411.getSuffixAPI().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getNumGovt() == null
						|| objWellbore1411.getNumGovt() != null && objWellbore1411.getNumGovt().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getStatusWellbore() == null || objWellbore1411.getStatusWellbore() != null
						&& objWellbore1411.getStatusWellbore().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getPurposeWellbore() == null || objWellbore1411.getPurposeWellbore() != null
						&& objWellbore1411.getPurposeWellbore().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getTypeWellbore() == null
						|| objWellbore1411.getTypeWellbore() != null && objWellbore1411.getTypeWellbore().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getShape() == null
						|| objWellbore1411.getShape() != null && objWellbore1411.getShape().isEmpty()) {
					result = true;
					break;
				}
				try {
					if (objWellbore1411.getDTimKickoff() == null
							|| objWellbore1411.getDTimKickoff() != null && objWellbore1411.getDTimKickoff().isEmpty()) {
						result = true;
						break;
					}
				} catch (DatatypeConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (objWellbore1411.getMdPlanned() == null || objWellbore1411.getMdPlanned() != null
						&& objWellbore1411.getMdPlanned().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getTvdPlanned() == null || objWellbore1411.getTvdPlanned() != null
						&& objWellbore1411.getTvdPlanned().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getMdSubSeaPlanned() == null || objWellbore1411.getMdSubSeaPlanned() != null
						&& objWellbore1411.getMdSubSeaPlanned().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getTvdSubSeaPlanned() == null || objWellbore1411.getTvdSubSeaPlanned() != null
						&& objWellbore1411.getTvdSubSeaPlanned().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objWellbore1411.getDayTarget() == null || objWellbore1411.getDayTarget() != null
						&& objWellbore1411.getDayTarget().toString().isEmpty()) {
					result = true;
					break;
				}
				com.hashmapinc.tempus.WitsmlObjects.v1411.CsCommonData commonData = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsCommonData) objWellbore1411
						.getCommonData();

				if (commonData.getDTimCreation() == null
						|| commonData.getDTimCreation() != null && commonData.getDTimCreation().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getDTimLastChange() == null || commonData.getDTimLastChange() != null
						&& commonData.getDTimLastChange().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getItemState() == null
						|| commonData.getItemState() != null && commonData.getItemState().toString().isEmpty()) {
					result = true;
					break;
				}
				if (commonData.getComments() == null
						|| commonData.getComments() != null && commonData.getComments().toString().isEmpty()) {
					result = true;
					break;
				}

			}
		}

		return result;
	}

	static boolean checkNotNullNodeTrjaectory(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjTrajectory) {
				LOG.info("checking Trajectory object ");
				ObjTrajectory objTraj311 = (ObjTrajectory) abstractWitsmlObject;

				if (objTraj311.getName() == null || objTraj311.getName() != null & objTraj311.getName().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getNameWell() == null
						|| objTraj311.getNameWell() != null & objTraj311.getNameWell().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getNameWellbore() == null
						|| objTraj311.getNameWellbore() != null & objTraj311.getNameWellbore().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getDTimTrajStart() == null
						|| objTraj311.getDTimTrajStart() != null & objTraj311.getDTimTrajStart().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getDTimTrajEnd() == null
						|| objTraj311.getDTimTrajEnd() != null & objTraj311.getDTimTrajEnd().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getMdMn() == null
						|| objTraj311.getMdMn() != null & objTraj311.getMdMn().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getMdMx() == null
						|| objTraj311.getMdMx() != null & objTraj311.getMdMx().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getServiceCompany() == null || objTraj311.getServiceCompany() != null
						& objTraj311.getServiceCompany().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getMagDeclUsed() == null
						|| objTraj311.getMagDeclUsed() != null & objTraj311.getMagDeclUsed().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getGridCorUsed() == null
						|| objTraj311.getGridCorUsed() != null & objTraj311.getGridCorUsed().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getAziVertSect() == null
						|| objTraj311.getAziVertSect() != null & objTraj311.getAziVertSect().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getDispEwVertSectOrig() == null || objTraj311.getDispEwVertSectOrig() != null
						& objTraj311.getDispEwVertSectOrig().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getDispNsVertSectOrig() == null || objTraj311.getDispNsVertSectOrig() != null
						& objTraj311.getDispNsVertSectOrig().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getAziRef() == null
						|| objTraj311.getAziRef() != null & objTraj311.getAziRef().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj311.getDispNsVertSectOrig() == null || objTraj311.getDispNsVertSectOrig() != null
						& objTraj311.getDispNsVertSectOrig().toString().isEmpty()) {
					result = true;
					break;
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) {

				com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory objTraj1411 = (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject;

				if (objTraj1411.getName() == null || objTraj1411.getName() != null & objTraj1411.getName().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getNameWell() == null
						|| objTraj1411.getNameWell() != null & objTraj1411.getNameWell().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getNameWellbore() == null
						|| objTraj1411.getNameWellbore() != null & objTraj1411.getNameWellbore().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getDTimTrajStart() == null || objTraj1411.getDTimTrajStart() != null
						& objTraj1411.getDTimTrajStart().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getDTimTrajEnd() == null
						|| objTraj1411.getDTimTrajEnd() != null & objTraj1411.getDTimTrajEnd().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getMdMn() == null
						|| objTraj1411.getMdMn() != null & objTraj1411.getMdMn().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getMdMx() == null
						|| objTraj1411.getMdMx() != null & objTraj1411.getMdMx().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getServiceCompany() == null || objTraj1411.getServiceCompany() != null
						& objTraj1411.getServiceCompany().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getMagDeclUsed() == null
						|| objTraj1411.getMagDeclUsed() != null & objTraj1411.getMagDeclUsed().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getGridCorUsed() == null
						|| objTraj1411.getGridCorUsed() != null & objTraj1411.getGridCorUsed().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getAziVertSect() == null
						|| objTraj1411.getAziVertSect() != null & objTraj1411.getAziVertSect().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getDispEwVertSectOrig() == null || objTraj1411.getDispEwVertSectOrig() != null
						& objTraj1411.getDispEwVertSectOrig().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getDispNsVertSectOrig() == null || objTraj1411.getDispNsVertSectOrig() != null
						& objTraj1411.getDispNsVertSectOrig().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getAziRef() == null
						|| objTraj1411.getAziRef() != null & objTraj1411.getAziRef().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1411.getDispNsVertSectOrig() == null || objTraj1411.getDispNsVertSectOrig() != null
						& objTraj1411.getDispNsVertSectOrig().toString().isEmpty()) {
					result = true;
					break;
				}

			}
		}

		return result;
	}

	static boolean checkNotNullUidForLog(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjLog) {
				LOG.info("checking Log object ");
				ObjLog objLog1311 = (ObjLog) abstractWitsmlObject;

				if (objLog1311.getName() == null || objLog1311.getName() != null && objLog1311.getName().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getNameWell() == null
						|| objLog1311.getNameWell() != null && objLog1311.getNameWell().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getNameWellbore() == null
						|| objLog1311.getNameWellbore() != null && objLog1311.getNameWellbore().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getServiceCompany() == null
						|| objLog1311.getServiceCompany() != null && objLog1311.getServiceCompany().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getRunNumber() == null
						|| objLog1311.getRunNumber() != null && objLog1311.getRunNumber().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getCreationDate() == null
						|| objLog1311.getCreationDate() != null && objLog1311.getCreationDate().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getDescription() == null
						|| objLog1311.getDescription() != null && objLog1311.getDescription().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getIndexType() == null
						|| objLog1311.getIndexType() != null && objLog1311.getIndexType().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getStartIndex() == null
						|| objLog1311.getStartIndex() != null && objLog1311.getStartIndex().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getEndIndex() == null
						|| objLog1311.getEndIndex() != null && objLog1311.getEndIndex().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getStepIncrement() == null || objLog1311.getStepIncrement() != null
						&& objLog1311.getStepIncrement().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getDirection() == null
						|| objLog1311.getDirection() != null && objLog1311.getDirection().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getIndexCurve() == null
						|| objLog1311.getIndexCurve() != null && objLog1311.getIndexCurve().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getNullValue() == null
						|| objLog1311.getNullValue() != null && objLog1311.getNullValue().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getLogParam() == null
						|| objLog1311.getLogParam() != null && objLog1311.getLogParam().toString().isEmpty()) {
					result = true;
					break;
				}
				List<CsLogCurveInfo> logCurveInfo = objLog1311.getLogCurveInfo();
				for (CsLogCurveInfo curveInfo : logCurveInfo) {
					if (curveInfo.getMnemonic() == null
							|| curveInfo.getMnemonic() != null && curveInfo.getMnemonic().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getClassWitsml() == null
							|| curveInfo.getClassWitsml() != null && curveInfo.getClassWitsml().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getMnemAlias() == null
							|| curveInfo.getMnemAlias() != null && curveInfo.getMnemAlias().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getUnit() == null || curveInfo.getUnit() != null && curveInfo.getUnit().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getNullValue() == null
							|| curveInfo.getNullValue() != null && curveInfo.getNullValue().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getMinIndex() == null
							|| curveInfo.getMinIndex() != null && curveInfo.getMinIndex().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getMaxIndex() == null
							|| curveInfo.getMaxIndex() != null && curveInfo.getMaxIndex().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getCurveDescription() == null || curveInfo.getCurveDescription() != null
							&& curveInfo.getCurveDescription().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getSensorOffset() == null || curveInfo.getSensorOffset() != null
							&& curveInfo.getSensorOffset().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getTraceState() == null
							|| curveInfo.getTraceState() != null && curveInfo.getTraceState().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getTypeLogData() == null
							|| curveInfo.getTypeLogData() != null && curveInfo.getTypeLogData().toString().isEmpty()) {
						result = true;
						break;
					}
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) {

				com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog objLog1411 = (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject;

				if (objLog1411.getName() == null || objLog1411.getName() != null && objLog1411.getName().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getNameWell() == null
						|| objLog1411.getNameWell() != null && objLog1411.getNameWell().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getNameWellbore() == null
						|| objLog1411.getNameWellbore() != null && objLog1411.getNameWellbore().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getServiceCompany() == null
						|| objLog1411.getServiceCompany() != null && objLog1411.getServiceCompany().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getRunNumber() == null
						|| objLog1411.getRunNumber() != null && objLog1411.getRunNumber().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getCreationDate() == null
						|| objLog1411.getCreationDate() != null && objLog1411.getCreationDate().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getDescription() == null
						|| objLog1411.getDescription() != null && objLog1411.getDescription().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getIndexType() == null
						|| objLog1411.getIndexType() != null && objLog1411.getIndexType().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getStartIndex() == null
						|| objLog1411.getStartIndex() != null && objLog1411.getStartIndex().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getEndIndex() == null
						|| objLog1411.getEndIndex() != null && objLog1411.getEndIndex().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getStepIncrement() == null || objLog1411.getStepIncrement() != null
						&& objLog1411.getStepIncrement().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getDirection() == null
						|| objLog1411.getDirection() != null && objLog1411.getDirection().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getIndexCurve() == null
						|| objLog1411.getIndexCurve() != null && objLog1411.getIndexCurve().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getNullValue() == null
						|| objLog1411.getNullValue() != null && objLog1411.getNullValue().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1411.getLogParam() == null
						|| objLog1411.getLogParam() != null && objLog1411.getLogParam().toString().isEmpty()) {
					result = true;
					break;
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> logCurveInfo = objLog1411
						.getLogCurveInfo();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo curveInfo : logCurveInfo) {
					if (curveInfo.getMnemonic() == null
							|| curveInfo.getMnemonic() != null && curveInfo.getMnemonic().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getClassWitsml() == null
							|| curveInfo.getClassWitsml() != null && curveInfo.getClassWitsml().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getMnemAlias() == null
							|| curveInfo.getMnemAlias() != null && curveInfo.getMnemAlias().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getUnit() == null || curveInfo.getUnit() != null && curveInfo.getUnit().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getNullValue() == null
							|| curveInfo.getNullValue() != null && curveInfo.getNullValue().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getMinIndex() == null
							|| curveInfo.getMinIndex() != null && curveInfo.getMinIndex().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getMaxIndex() == null
							|| curveInfo.getMaxIndex() != null && curveInfo.getMaxIndex().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getCurveDescription() == null || curveInfo.getCurveDescription() != null
							&& curveInfo.getCurveDescription().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getSensorOffset() == null || curveInfo.getSensorOffset() != null
							&& curveInfo.getSensorOffset().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getTraceState() == null
							|| curveInfo.getTraceState() != null && curveInfo.getTraceState().toString().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getTypeLogData() == null
							|| curveInfo.getTypeLogData() != null && curveInfo.getTypeLogData().toString().isEmpty()) {
						result = true;
						break;
					}
				}

			}
		}

		return result;
	}

	/**
	 * This method checks for unique UID in XMLin
	 * 
	 * @param XMLin
	 * @return true if unique else false
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkUniqueUid(String XMLin, String WMLTypein) {

		boolean result = false;
		List<AbstractWitsmlObject> witsmlObjects;
		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			witsmlObjects = WitsmlObjectParser.parse(WMLTypein, XMLin, version);
			switch (WMLTypein) {
			case "log":
				result = checkUniqueUidForLog(witsmlObjects);
				break;
			case "trajectory":
				result = checkUniqueUidForTrajectory(witsmlObjects);
				break;
			case "well":
				result = checkUniqueUidForWell(witsmlObjects);
				break;
			case "wellbore":
				result = false;
				break;

			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is " + e.getMessage());
		}
		return result;
	}

	static boolean checkUniqueUidForTrajectory(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;
		Set<String> checkDuplicateSet = new HashSet<>();
		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjTrajectory) {
				List<CsTrajectoryStation> trajectoryStationList = ((ObjTrajectory) abstractWitsmlObject)
						.getTrajectoryStation();
				for (CsTrajectoryStation trajectoryStation : trajectoryStationList) {
					if (checkDuplicateSet.add(trajectoryStation.getUid()) == false) {
						result = true;
						break;
					}
					checkDuplicateSet.add(trajectoryStation.getUid());
				}
			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) {
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsTrajectoryStation> trajectoryStationList = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getTrajectoryStation();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsTrajectoryStation trajectoryStation : trajectoryStationList) {
					if (checkDuplicateSet.add(trajectoryStation.getUid()) == false) {
						result = true;
						break;
					}
					checkDuplicateSet.add(trajectoryStation.getUid());
				}
			}
		}

		return result;
	}

	static boolean checkUniqueUidForWell(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;
		Set<String> checkDuplicateSet = new HashSet<>();
		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWell) {
				List<CsReferencePoint> wellRefrenceinfo = ((ObjWell) abstractWitsmlObject).getReferencePoint();
				for (CsReferencePoint refrencePoint : wellRefrenceinfo) {
					if (checkDuplicateSet.add(refrencePoint.getUid()) == false) {
						result = true;
						break;
					}
					checkDuplicateSet.add(refrencePoint.getUid());
				}
			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) {
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint> wellRefrenceinfo = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getReferencePoint();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint refrencePoint : wellRefrenceinfo) {
					if (checkDuplicateSet.add(refrencePoint.getUid()) == false) {
						result = true;
						break;
					}
					checkDuplicateSet.add(refrencePoint.getUid());
				}
			}
		}

		return result;

	}

	static boolean checkUniqueUidForLog(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;
		Set<String> checkDuplicateSet = new HashSet<>();
		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjLog) {
				List<CsLogCurveInfo> logCurveInfos = ((ObjLog) abstractWitsmlObject).getLogCurveInfo();
				for (CsLogCurveInfo csLogCurveInfo : logCurveInfos) {
					if (checkDuplicateSet.add(csLogCurveInfo.getUid()) == false) {
						result = true;
						break;
					}
					checkDuplicateSet.add(csLogCurveInfo.getUid());
				}
			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) {
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> logCurveInfos = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
						.getLogCurveInfo();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo csLogCurveInfo : logCurveInfos) {
					if (checkDuplicateSet.add(csLogCurveInfo.getUid()) == false) {
						result = true;
						break;
					}
					checkDuplicateSet.add(csLogCurveInfo.getUid());
				}
			}
		}

		return result;
	}

	/**
	 * This method checks for UOM attribute to be null.
	 * 
	 * @param XMLin
	 * @return true if null else false
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkNotNullUOM(String XMLin, String WMLTypein) {
		boolean result = false;
		List<AbstractWitsmlObject> witsmlObjects;
		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			witsmlObjects = WitsmlObjectParser.parse(WMLTypein, XMLin, version);
			switch (WMLTypein) {
			case "log":
				result = checkNotNullUOMForDiffVersionLog(witsmlObjects);
				break;
			case "trajectory":
				result = checkNotNullUOMForDiffVersionTrajectory(witsmlObjects);
				break;
			case "well":
				result = checkNotNullUOMForDiffVersionWell(witsmlObjects);
				break;
			case "wellbore":
				result = checkNotNullUOMForDiffVersionWellBore(witsmlObjects);
				break;
			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is " + e.getMessage());
		}
		return result;
	}

	static boolean checkNotNullUOMForDiffVersionTrajectory(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjTrajectory) {
				LOG.info("checking Trajectory object");
				ObjTrajectory objTraj1311 = (ObjTrajectory) abstractWitsmlObject;

				if (objTraj1311.getMdMn().getUom() == null || objTraj1311.getMdMn().getUom() != null
						&& objTraj1311.getMdMn().getUom().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1311.getMdMx().getUom() == null || objTraj1311.getMdMx().getUom() != null
						&& objTraj1311.getMdMx().getUom().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1311.getMagDeclUsed().getUom() == null || objTraj1311.getMagDeclUsed().getUom() != null
						&& objTraj1311.getMagDeclUsed().getUom().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1311.getGridCorUsed().getUom() == null || objTraj1311.getGridCorUsed().getUom() != null
						&& objTraj1311.getGridCorUsed().getUom().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1311.getAziVertSect().getUom() == null || objTraj1311.getAziVertSect().getUom() != null
						&& objTraj1311.getAziVertSect().getUom().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1311.getDispNsVertSectOrig().getUom() == null
						|| objTraj1311.getDispNsVertSectOrig().getUom() != null
								&& objTraj1311.getDispNsVertSectOrig().getUom().toString().isEmpty()) {
					result = true;
					break;
				}
				if (objTraj1311.getDispEwVertSectOrig().getUom() == null
						|| objTraj1311.getDispEwVertSectOrig().getUom() != null
								&& objTraj1311.getDispEwVertSectOrig().getUom().toString().isEmpty()) {
					result = true;
					break;
				}
				List<CsTrajectoryStation> trajectoryStation = objTraj1311.getTrajectoryStation();
				for (CsTrajectoryStation trajStation : trajectoryStation) {
					if (trajStation.getMd().getUom() == null || trajStation.getMd().getUom() != null
							&& trajStation.getMd().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getTvd().getUom() == null || trajStation.getTvd().getUom() != null
							&& trajStation.getTvd().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getIncl().getUom() == null || trajStation.getIncl().getUom() != null
							&& trajStation.getIncl().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getAzi().getUom() == null || trajStation.getAzi().getUom() != null
							&& trajStation.getAzi().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getMtf().getUom() == null || trajStation.getMtf().getUom() != null
							&& trajStation.getMtf().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getGtf().getUom() == null || trajStation.getGtf().getUom() != null
							&& trajStation.getGtf().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getDispEw().getUom() == null || trajStation.getDispEw().getUom() != null
							&& trajStation.getDispEw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getDispNs().getUom() == null || trajStation.getDispNs().getUom() != null
							&& trajStation.getDispNs().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getVertSect().getUom() == null || trajStation.getVertSect().getUom() != null
							&& trajStation.getVertSect().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getDls().getUom() == null || trajStation.getDls().getUom() != null
							&& trajStation.getDls().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getRateTurn().getUom() == null || trajStation.getRateTurn().getUom() != null
							&& trajStation.getRateTurn().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getRateBuild().getUom() == null || trajStation.getRateBuild().getUom() != null
							&& trajStation.getRateBuild().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getMdDelta().getUom() == null || trajStation.getMdDelta().getUom() != null
							&& trajStation.getMdDelta().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getTvdDelta().getUom() == null || trajStation.getTvdDelta().getUom() != null
							&& trajStation.getTvdDelta().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getGravTotalUncert().getUom() == null
							|| trajStation.getGravTotalUncert().getUom() != null
									&& trajStation.getGravTotalUncert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getDipAngleUncert().getUom() == null
							|| trajStation.getDipAngleUncert().getUom() != null
									&& trajStation.getDipAngleUncert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getMagTotalUncert().getUom() == null
							|| trajStation.getMagTotalUncert().getUom() != null
									&& trajStation.getMagTotalUncert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					CsStnTrajRawData trajRawData = (CsStnTrajRawData) trajStation.getRawData();

					if (trajRawData.getGravAxialRaw().getUom() == null || trajRawData.getGravAxialRaw().getUom() != null
							&& trajRawData.getGravAxialRaw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajRawData.getGravTran1Raw().getUom() == null || trajRawData.getGravTran1Raw().getUom() != null
							&& trajRawData.getGravTran1Raw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajRawData.getGravTran2Raw().getUom() == null || trajRawData.getGravTran2Raw().getUom() != null
							&& trajRawData.getGravTran2Raw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajRawData.getMagAxialRaw().getUom() == null || trajRawData.getMagAxialRaw().getUom() != null
							&& trajRawData.getMagAxialRaw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajRawData.getMagTran1Raw().getUom() == null || trajRawData.getMagTran1Raw().getUom() != null
							&& trajRawData.getMagTran1Raw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajRawData.getMagTran2Raw().getUom() == null || trajRawData.getMagTran2Raw().getUom() != null
							&& trajRawData.getMagTran2Raw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}

					CsStnTrajCorUsed crUSed = (CsStnTrajCorUsed) trajStation.getCorUsed();

					if (crUSed.getGravAxialAccelCor().getUom() == null || crUSed.getGravAxialAccelCor().getUom() != null
							&& crUSed.getGravAxialAccelCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getGravTran1AccelCor().getUom() == null || crUSed.getGravTran1AccelCor().getUom() != null
							&& crUSed.getGravTran1AccelCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getGravTran2AccelCor().getUom() == null || crUSed.getGravTran2AccelCor().getUom() != null
							&& crUSed.getGravTran2AccelCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getMagAxialDrlstrCor().getUom() == null || crUSed.getMagAxialDrlstrCor().getUom() != null
							&& crUSed.getMagAxialDrlstrCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getMagTran1DrlstrCor().getUom() == null || crUSed.getMagTran1DrlstrCor().getUom() != null
							&& crUSed.getMagTran1DrlstrCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getMagTran2DrlstrCor().getUom() == null || crUSed.getMagTran2DrlstrCor().getUom() != null
							&& crUSed.getMagTran2DrlstrCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getSagAziCor().getUom() == null || crUSed.getSagAziCor().getUom() != null
							&& crUSed.getSagAziCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getSagIncCor().getUom() == null || crUSed.getSagIncCor().getUom() != null
							&& crUSed.getSagIncCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getStnGridCorUsed().getUom() == null || crUSed.getStnGridCorUsed().getUom() != null
							&& crUSed.getStnGridCorUsed().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getStnMagDeclUsed().getUom() == null || crUSed.getStnMagDeclUsed().getUom() != null
							&& crUSed.getStnMagDeclUsed().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getDirSensorOffset().getUom() == null || crUSed.getDirSensorOffset().getUom() != null
							&& crUSed.getDirSensorOffset().getUom().toString().isEmpty()) {
						result = true;
						break;
					}

					CsStnTrajValid stnValid = (CsStnTrajValid) trajStation.getValid();

					if (stnValid.getMagTotalFieldCalc().getUom() == null
							|| stnValid.getMagTotalFieldCalc().getUom() != null
									&& stnValid.getMagTotalFieldCalc().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (stnValid.getMagDipAngleCalc().getUom() == null || stnValid.getMagDipAngleCalc().getUom() != null
							&& stnValid.getMagDipAngleCalc().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (stnValid.getGravTotalFieldCalc().getUom() == null
							|| stnValid.getGravTotalFieldCalc().getUom() != null
									&& stnValid.getGravTotalFieldCalc().getUom().toString().isEmpty()) {
						result = true;
						break;
					}

					CsStnTrajMatrixCov matric = (CsStnTrajMatrixCov) trajStation.getMatrixCov();

					if (matric.getVarianceEE().getUom() == null || matric.getVarianceEE().getUom() != null
							&& matric.getVarianceEE().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getVarianceNE().getUom() == null || matric.getVarianceNE().getUom() != null
							&& matric.getVarianceNE().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getVarianceNN().getUom() == null || matric.getVarianceNN().getUom() != null
							&& matric.getVarianceNN().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getVarianceEVert().getUom() == null || matric.getVarianceEVert().getUom() != null
							&& matric.getVarianceEVert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getVarianceNVert().getUom() == null || matric.getVarianceNVert().getUom() != null
							&& matric.getVarianceNVert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getVarianceVertVert().getUom() == null || matric.getVarianceVertVert().getUom() != null
							&& matric.getVarianceVertVert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getBiasE().getUom() == null
							|| matric.getBiasE().getUom() != null && matric.getBiasE().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getBiasN().getUom() == null
							|| matric.getBiasN().getUom() != null && matric.getBiasN().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getBiasVert().getUom() == null || matric.getBiasVert().getUom() != null
							&& matric.getBiasVert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}

					List<CsLocation> location = trajStation.getLocation();
					for (CsLocation loc : location) {
						if (loc.getLatitude().getUom() == null || loc.getLatitude().getUom() != null
								&& loc.getLatitude().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
						if (loc.getLongitude().getUom() == null || loc.getLongitude().getUom() != null
								&& loc.getLongitude().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
						if (loc.getEasting().getUom() == null || loc.getEasting().getUom() != null
								&& loc.getEasting().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
						if (loc.getNorthing().getUom() == null || loc.getNorthing().getUom() != null
								&& loc.getNorthing().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
						if (loc.getLocalX().getUom() == null
								|| loc.getLocalX().getUom() != null && loc.getLocalX().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
						if (loc.getLocalY().getUom() == null
								|| loc.getLocalY().getUom() != null && loc.getLocalY().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
					}

				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) {
				if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject).getMdMn()
						.getUom() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject).getMdMn()
								.getUom() != null
								&& (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
										.getMdMn().getUom().toString().isEmpty()))) {
					result = true;
					break;
				}
				if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject).getMdMx()
						.getUom() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject).getMdMx()
								.getUom() != null
								&& (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
										.getMdMx().getUom().toString().isEmpty()))) {
					result = true;
					break;
				}
				if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject).getMagDeclUsed()
						.getUom() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
								.getMagDeclUsed().getUom() != null
								&& (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
										.getMagDeclUsed().getUom().toString().isEmpty()))) {
					result = true;
					break;
				}
				if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject).getGridCorUsed()
						.getUom() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
								.getGridCorUsed().getUom() != null
								&& (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
										.getGridCorUsed().getUom().toString().isEmpty()))) {
					result = true;
					break;
				}
				if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject).getAziVertSect()
						.getUom() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
								.getAziVertSect().getUom() != null
								&& (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
										.getAziVertSect().getUom().toString().isEmpty()))) {
					result = true;
					break;
				}
				if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getDispNsVertSectOrig().getUom() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
								.getDispNsVertSectOrig().getUom() != null
								&& (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
										.getDispNsVertSectOrig().getUom().toString().isEmpty()))) {
					result = true;
					break;
				}
				if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getDispEwVertSectOrig().getUom() == null
						|| (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
								.getDispEwVertSectOrig().getUom() != null
								&& (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
										.getDispEwVertSectOrig().getUom().toString().isEmpty()))) {
					result = true;
					break;
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsTrajectoryStation> trajectoryStation = (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject)
						.getTrajectoryStation());
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsTrajectoryStation trajStation : trajectoryStation) {
					if (trajStation.getMd().getUom() == null || trajStation.getMd().getUom() != null
							&& trajStation.getMd().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getTvd().getUom() == null || trajStation.getTvd().getUom() != null
							&& trajStation.getTvd().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getIncl().getUom() == null || trajStation.getIncl().getUom() != null
							&& trajStation.getIncl().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getAzi().getUom() == null || trajStation.getAzi().getUom() != null
							&& trajStation.getAzi().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getMtf().getUom() == null || trajStation.getMtf().getUom() != null
							&& trajStation.getMtf().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getGtf().getUom() == null || trajStation.getGtf().getUom() != null
							&& trajStation.getGtf().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getDispEw().getUom() == null || trajStation.getDispEw().getUom() != null
							&& trajStation.getDispEw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getDispNs().getUom() == null || trajStation.getDispNs().getUom() != null
							&& trajStation.getDispNs().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getVertSect().getUom() == null || trajStation.getVertSect().getUom() != null
							&& trajStation.getVertSect().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getDls().getUom() == null || trajStation.getDls().getUom() != null
							&& trajStation.getDls().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getRateTurn().getUom() == null || trajStation.getRateTurn().getUom() != null
							&& trajStation.getRateTurn().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getRateBuild().getUom() == null || trajStation.getRateBuild().getUom() != null
							&& trajStation.getRateBuild().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getMdDelta().getUom() == null || trajStation.getMdDelta().getUom() != null
							&& trajStation.getMdDelta().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getTvdDelta().getUom() == null || trajStation.getTvdDelta().getUom() != null
							&& trajStation.getTvdDelta().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getGravTotalUncert().getUom() == null
							|| trajStation.getGravTotalUncert().getUom() != null
									&& trajStation.getGravTotalUncert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getDipAngleUncert().getUom() == null
							|| trajStation.getDipAngleUncert().getUom() != null
									&& trajStation.getDipAngleUncert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajStation.getMagTotalUncert().getUom() == null
							|| trajStation.getMagTotalUncert().getUom() != null
									&& trajStation.getMagTotalUncert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajRawData trajRawData = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajRawData) trajStation
							.getRawData();

					if (trajRawData.getGravAxialRaw().getUom() == null || trajRawData.getGravAxialRaw().getUom() != null
							&& trajRawData.getGravAxialRaw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajRawData.getGravTran1Raw().getUom() == null || trajRawData.getGravTran1Raw().getUom() != null
							&& trajRawData.getGravTran1Raw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajRawData.getGravTran2Raw().getUom() == null || trajRawData.getGravTran2Raw().getUom() != null
							&& trajRawData.getGravTran2Raw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajRawData.getMagAxialRaw().getUom() == null || trajRawData.getMagAxialRaw().getUom() != null
							&& trajRawData.getMagAxialRaw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajRawData.getMagTran1Raw().getUom() == null || trajRawData.getMagTran1Raw().getUom() != null
							&& trajRawData.getMagTran1Raw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (trajRawData.getMagTran2Raw().getUom() == null || trajRawData.getMagTran2Raw().getUom() != null
							&& trajRawData.getMagTran2Raw().getUom().toString().isEmpty()) {
						result = true;
						break;
					}

					com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajCorUsed crUSed = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajCorUsed) trajStation
							.getCorUsed();

					if (crUSed.getGravAxialAccelCor().getUom() == null || crUSed.getGravAxialAccelCor().getUom() != null
							&& crUSed.getGravAxialAccelCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getGravTran1AccelCor().getUom() == null || crUSed.getGravTran1AccelCor().getUom() != null
							&& crUSed.getGravTran1AccelCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getGravTran2AccelCor().getUom() == null || crUSed.getGravTran2AccelCor().getUom() != null
							&& crUSed.getGravTran2AccelCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getMagAxialDrlstrCor().getUom() == null || crUSed.getMagAxialDrlstrCor().getUom() != null
							&& crUSed.getMagAxialDrlstrCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getMagTran1DrlstrCor().getUom() == null || crUSed.getMagTran1DrlstrCor().getUom() != null
							&& crUSed.getMagTran1DrlstrCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getMagTran2DrlstrCor().getUom() == null || crUSed.getMagTran2DrlstrCor().getUom() != null
							&& crUSed.getMagTran2DrlstrCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getSagAziCor().getUom() == null || crUSed.getSagAziCor().getUom() != null
							&& crUSed.getSagAziCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getSagIncCor().getUom() == null || crUSed.getSagIncCor().getUom() != null
							&& crUSed.getSagIncCor().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getStnGridCorUsed().getUom() == null || crUSed.getStnGridCorUsed().getUom() != null
							&& crUSed.getStnGridCorUsed().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getStnMagDeclUsed().getUom() == null || crUSed.getStnMagDeclUsed().getUom() != null
							&& crUSed.getStnMagDeclUsed().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (crUSed.getDirSensorOffset().getUom() == null || crUSed.getDirSensorOffset().getUom() != null
							&& crUSed.getDirSensorOffset().getUom().toString().isEmpty()) {
						result = true;
						break;
					}

					com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajValid stnValid = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajValid) trajStation
							.getValid();

					if (stnValid.getMagTotalFieldCalc().getUom() == null
							|| stnValid.getMagTotalFieldCalc().getUom() != null
									&& stnValid.getMagTotalFieldCalc().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (stnValid.getMagDipAngleCalc().getUom() == null || stnValid.getMagDipAngleCalc().getUom() != null
							&& stnValid.getMagDipAngleCalc().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (stnValid.getGravTotalFieldCalc().getUom() == null
							|| stnValid.getGravTotalFieldCalc().getUom() != null
									&& stnValid.getGravTotalFieldCalc().getUom().toString().isEmpty()) {
						result = true;
						break;
					}

					com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajMatrixCov matric = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsStnTrajMatrixCov) trajStation
							.getMatrixCov();

					if (matric.getVarianceEE().getUom() == null || matric.getVarianceEE().getUom() != null
							&& matric.getVarianceEE().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getVarianceNE().getUom() == null || matric.getVarianceNE().getUom() != null
							&& matric.getVarianceNE().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getVarianceNN().getUom() == null || matric.getVarianceNN().getUom() != null
							&& matric.getVarianceNN().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getVarianceEVert().getUom() == null || matric.getVarianceEVert().getUom() != null
							&& matric.getVarianceEVert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getVarianceNVert().getUom() == null || matric.getVarianceNVert().getUom() != null
							&& matric.getVarianceNVert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getVarianceVertVert().getUom() == null || matric.getVarianceVertVert().getUom() != null
							&& matric.getVarianceVertVert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getBiasE().getUom() == null
							|| matric.getBiasE().getUom() != null && matric.getBiasE().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getBiasN().getUom() == null
							|| matric.getBiasN().getUom() != null && matric.getBiasN().getUom().toString().isEmpty()) {
						result = true;
						break;
					}
					if (matric.getBiasVert().getUom() == null || matric.getBiasVert().getUom() != null
							&& matric.getBiasVert().getUom().toString().isEmpty()) {
						result = true;
						break;
					}

					List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation> location = trajStation.getLocation();
					for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation loc : location) {
						if (loc.getLatitude().getUom() == null || loc.getLatitude().getUom() != null
								&& loc.getLatitude().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
						if (loc.getLongitude().getUom() == null || loc.getLongitude().getUom() != null
								&& loc.getLongitude().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
						if (loc.getEasting().getUom() == null || loc.getEasting().getUom() != null
								&& loc.getEasting().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
						if (loc.getNorthing().getUom() == null || loc.getNorthing().getUom() != null
								&& loc.getNorthing().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
						if (loc.getLocalX().getUom() == null
								|| loc.getLocalX().getUom() != null && loc.getLocalX().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
						if (loc.getLocalY().getUom() == null
								|| loc.getLocalY().getUom() != null && loc.getLocalY().getUom().toString().isEmpty()) {
							result = true;
							break;
						}
					}

				}
			}
		}

		return result;
	}

	static boolean checkNotNullUOMForDiffVersionLog(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjLog) {
				LOG.info("checking log object");
				ObjLog objLog1311 = (ObjLog) abstractWitsmlObject;

				if (objLog1311.getStartIndex().getUom() == null || objLog1311.getStartIndex().getUom() != null
						&& objLog1311.getStartIndex().getUom().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getEndIndex().getUom() == null
						|| objLog1311.getEndIndex().getUom() != null && objLog1311.getEndIndex().getUom().isEmpty()) {
					result = true;
					break;
				}
				if (objLog1311.getStepIncrement().getUom() == null || objLog1311.getStepIncrement().getUom() != null
						&& objLog1311.getStepIncrement().getUom().isEmpty()) {
					result = true;
					break;
				}
				List<CsLogCurveInfo> logCurveInfo = objLog1311.getLogCurveInfo();
				for (CsLogCurveInfo curveInfo : logCurveInfo) {
					if (curveInfo.getMinIndex().getUom() == null
							|| curveInfo.getMinIndex().getUom() != null && curveInfo.getMinIndex().getUom().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getMaxIndex().getUom() == null
							|| curveInfo.getMaxIndex().getUom() != null && curveInfo.getMaxIndex().getUom().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getSensorOffset().getUom() == null || curveInfo.getSensorOffset().getUom() != null
							&& curveInfo.getSensorOffset().getUom().isEmpty()) {
						result = true;
						break;
					}
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) {
				if ((((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject).getStartIndex()
						.getUom() == null
						|| ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject).getStartIndex()
								.getUom() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
										.getStartIndex().getUom().isEmpty())) {
					result = true;
					break;
				}
				if ((((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject).getEndIndex()
						.getUom() == null
						|| ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject).getEndIndex()
								.getUom() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
										.getEndIndex().getUom().isEmpty())) {
					result = true;
					break;
				}
				if ((((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject).getStepIncrement()
						.getUom() == null
						|| ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject).getStepIncrement()
								.getUom() != null
								&& ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
										.getStepIncrement().getUom().isEmpty())) {
					result = true;
					break;
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> logCurveInfo = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
						.getLogCurveInfo();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo curveInfo : logCurveInfo) {
					if (curveInfo.getMinIndex().getUom() == null
							|| curveInfo.getMinIndex().getUom() != null && curveInfo.getMinIndex().getUom().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getMaxIndex().getUom() == null
							|| curveInfo.getMaxIndex().getUom() != null && curveInfo.getMaxIndex().getUom().isEmpty()) {
						result = true;
						break;
					}
					if (curveInfo.getSensorOffset().getUom() == null || curveInfo.getSensorOffset().getUom() != null
							&& curveInfo.getSensorOffset().getUom().isEmpty()) {
						result = true;
						break;
					}
				}
			}
		}

		return result;
	}

	static boolean checkNotNullUOMForDiffVersionWell(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWell) {
				LOG.info("checking well object ");
				ObjWell objWell1311 = (ObjWell) abstractWitsmlObject;

				DimensionlessMeasure pcInterest = (DimensionlessMeasure) objWell1311.getPcInterest();
				if (pcInterest.getUom() == null
						|| (pcInterest.getUom() != null && pcInterest.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				WellElevationCoord wellHeadElevation = (WellElevationCoord) objWell1311.getWellheadElevation();

				if (wellHeadElevation.getUom() == null
						|| (wellHeadElevation.getUom() != null && wellHeadElevation.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				WellElevationCoord groundElevation = (WellElevationCoord) objWell1311.getGroundElevation();

				if (groundElevation.getUom() == null
						|| (groundElevation.getUom() != null && groundElevation.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				WellVerticalDepthCoord waterDepth = (WellVerticalDepthCoord) objWell1311.getWaterDepth();

				if (waterDepth.getUom() == null
						|| (waterDepth.getUom() != null && waterDepth.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				WellDatum wellDatum = (WellDatum) objWell1311.getWellDatum();
				if (wellDatum.getElevation().getUom() == null || (wellDatum.getElevation().getUom() != null
						&& wellDatum.getElevation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				CsLocation wellLocation = (CsLocation) objWell1311.getWellLocation();
				if (wellLocation.getEasting().getUom() == null || (wellLocation.getEasting().getUom() != null
						&& wellLocation.getEasting().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellLocation.getNorthing().getUom() == null || (wellLocation.getNorthing().getUom() != null
						&& wellLocation.getNorthing().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellLocation.getLatitude().getUom() == null || (wellLocation.getLatitude().getUom() != null
						&& wellLocation.getLatitude().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellLocation.getLongitude().getUom() == null || (wellLocation.getLongitude().getUom() != null
						&& wellLocation.getLongitude().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellLocation.getLocalX().getUom() == null || (wellLocation.getLocalX().getUom() != null
						&& wellLocation.getLocalX().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellLocation.getLocalY().getUom() == null || (wellLocation.getLocalY().getUom() != null
						&& wellLocation.getLocalY().getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				CsReferencePoint refrencePoint = (CsReferencePoint) objWell1311.getReferencePoint();
				if (refrencePoint.getElevation().getUom() == null || (refrencePoint.getElevation().getUom() != null
						&& refrencePoint.getElevation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (refrencePoint.getMeasuredDepth().getUom() == null
						|| (refrencePoint.getMeasuredDepth().getUom() != null
								&& refrencePoint.getMeasuredDepth().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				CsLocation location = (CsLocation) refrencePoint.getLocation();
				if (location.getEasting().getUom() == null || (location.getEasting().getUom() != null
						&& location.getEasting().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (location.getNorthing().getUom() == null || (location.getNorthing().getUom() != null
						&& location.getNorthing().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (location.getLatitude().getUom() == null || (location.getLatitude().getUom() != null
						&& location.getLatitude().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (location.getLongitude().getUom() == null || (location.getLongitude().getUom() != null
						&& location.getLongitude().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (location.getLocalX().getUom() == null || (location.getLocalX().getUom() != null
						&& location.getLocalX().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (location.getLocalY().getUom() == null || (location.getLocalY().getUom() != null
						&& location.getLocalY().getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				CsWellCRS wellCRS = (CsWellCRS) objWell1311.getWellCRS();
				if (wellCRS.getGeographic().getXTranslation().getUom() == null
						|| (wellCRS.getGeographic().getXTranslation().getUom() != null
								&& wellCRS.getGeographic().getXTranslation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getYTranslation().getUom() == null
						|| (wellCRS.getGeographic().getYTranslation().getUom() != null
								&& wellCRS.getGeographic().getYTranslation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getZTranslation().getUom() == null
						|| (wellCRS.getGeographic().getZTranslation().getUom() != null
								&& wellCRS.getGeographic().getZTranslation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getXRotation().getUom() == null
						|| (wellCRS.getGeographic().getXRotation().getUom() != null
								&& wellCRS.getGeographic().getXRotation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getYRotation().getUom() == null
						|| (wellCRS.getGeographic().getYRotation().getUom() != null
								&& wellCRS.getGeographic().getYRotation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getZRotation().getUom() == null
						|| (wellCRS.getGeographic().getZRotation().getUom() != null
								&& wellCRS.getGeographic().getZRotation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getEllipsoidSemiMajorAxis().getUom() == null
						|| (wellCRS.getGeographic().getEllipsoidSemiMajorAxis().getUom() != null
								&& wellCRS.getGeographic().getEllipsoidSemiMajorAxis().getUom().toString().isEmpty())) {
					result = true;
					break;
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) {

				com.hashmapinc.tempus.WitsmlObjects.v1411.DimensionlessMeasure pcInterest = (com.hashmapinc.tempus.WitsmlObjects.v1411.DimensionlessMeasure) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getPcInterest();

				if (pcInterest.getUom() == null
						|| (pcInterest.getUom() != null && pcInterest.getUom().toString().isEmpty())) {

					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord wellHeadElevation = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellheadElevation();

				if (wellHeadElevation.getUom().toString() == null
						|| (wellHeadElevation.getUom() != null && wellHeadElevation.getUom().toString().isEmpty())) {

					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord groundElevation = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getGroundElevation();

				if (groundElevation.getUom() == null
						|| (groundElevation.getUom() != null && groundElevation.getUom().toString().isEmpty())) {

					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure waterDepth = (com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWaterDepth();

				if (waterDepth.getUom() == null
						|| (waterDepth.getUom() != null && waterDepth.getUom().toString().isEmpty())) {

					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum wellDatum = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellDatum();
				if (wellDatum.getElevation().getUom() == null || (wellDatum.getElevation().getUom() != null
						&& wellDatum.getElevation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation wellLocation = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellLocation();
				if (wellLocation.getEasting().getUom() == null || (wellLocation.getEasting().getUom() != null
						&& wellLocation.getEasting().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellLocation.getNorthing().getUom() == null || (wellLocation.getNorthing().getUom() != null
						&& wellLocation.getNorthing().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellLocation.getLatitude().getUom() == null || (wellLocation.getLatitude().getUom() != null
						&& wellLocation.getLatitude().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellLocation.getLongitude().getUom() == null || (wellLocation.getLongitude().getUom() != null
						&& wellLocation.getLongitude().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellLocation.getLocalX().getUom() == null || (wellLocation.getLocalX().getUom() != null
						&& wellLocation.getLocalX().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellLocation.getLocalY().getUom() == null || (wellLocation.getLocalY().getUom() != null
						&& wellLocation.getLocalY().getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint refrencePoint = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getReferencePoint();
				if (refrencePoint.getElevation().getUom() == null || (refrencePoint.getElevation().getUom() != null
						&& refrencePoint.getElevation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (refrencePoint.getMeasuredDepth().getUom() == null
						|| (refrencePoint.getMeasuredDepth().getUom() != null
								&& refrencePoint.getMeasuredDepth().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation location = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation) refrencePoint
						.getLocation();
				if (location.getEasting().getUom() == null || (location.getEasting().getUom() != null
						&& location.getEasting().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (location.getNorthing().getUom() == null || (location.getNorthing().getUom() != null
						&& location.getNorthing().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (location.getLatitude().getUom() == null || (location.getLatitude().getUom() != null
						&& location.getLatitude().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (location.getLongitude().getUom() == null || (location.getLongitude().getUom() != null
						&& location.getLongitude().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (location.getLocalX().getUom() == null || (location.getLocalX().getUom() != null
						&& location.getLocalX().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (location.getLocalY().getUom() == null || (location.getLocalY().getUom() != null
						&& location.getLocalY().getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS wellCRS = (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellCRS();
				if (wellCRS.getGeographic().getXTranslation().getUom() == null
						|| (wellCRS.getGeographic().getXTranslation().getUom() != null
								&& wellCRS.getGeographic().getXTranslation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getYTranslation().getUom() == null
						|| (wellCRS.getGeographic().getYTranslation().getUom() != null
								&& wellCRS.getGeographic().getYTranslation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getZTranslation().getUom() == null
						|| (wellCRS.getGeographic().getZTranslation().getUom() != null
								&& wellCRS.getGeographic().getZTranslation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getXRotation().getUom() == null
						|| (wellCRS.getGeographic().getXRotation().getUom() != null
								&& wellCRS.getGeographic().getXRotation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getYRotation().getUom() == null
						|| (wellCRS.getGeographic().getYRotation().getUom() != null
								&& wellCRS.getGeographic().getYRotation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getZRotation().getUom() == null
						|| (wellCRS.getGeographic().getZRotation().getUom() != null
								&& wellCRS.getGeographic().getZRotation().getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				if (wellCRS.getGeographic().getEllipsoidSemiMajorAxis().getUom() == null
						|| (wellCRS.getGeographic().getEllipsoidSemiMajorAxis().getUom() != null
								&& wellCRS.getGeographic().getEllipsoidSemiMajorAxis().getUom().toString().isEmpty())) {
					result = true;
					break;
				}

			}
		}

		return result;
	}

	static boolean checkNotNullUOMForDiffVersionWellBore(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWellbore) {
				LOG.info("checking wellBore object ");
				ObjWellbore objWellbore1311 = (ObjWellbore) abstractWitsmlObject;

				MeasuredDepthCoord mdCurrent = (MeasuredDepthCoord) objWellbore1311.getMdCurrent();

				if (mdCurrent.getUom() == null
						|| (mdCurrent.getUom() != null && mdCurrent.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				WellVerticalDepthCoord tvdCurrent = (WellVerticalDepthCoord) objWellbore1311.getTvdCurrent();

				if (tvdCurrent.getUom() == null
						|| (tvdCurrent.getUom() != null && tvdCurrent.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				MeasuredDepthCoord mdKickoff = (MeasuredDepthCoord) objWellbore1311.getMdKickoff();

				if (mdKickoff.getUom() == null
						|| (mdKickoff.getUom() != null && mdKickoff.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				WellVerticalDepthCoord tvdKickoff = (WellVerticalDepthCoord) objWellbore1311.getTvdKickoff();

				if (tvdKickoff.getUom() == null
						|| (tvdKickoff.getUom() != null && tvdKickoff.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				MeasuredDepthCoord mdPlanned = (MeasuredDepthCoord) objWellbore1311.getMdPlanned();

				if (mdPlanned.getUom() == null
						|| (mdPlanned.getUom() != null && mdPlanned.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				WellVerticalDepthCoord vdPlanned = (WellVerticalDepthCoord) objWellbore1311.getTvdPlanned();

				if (vdPlanned.getUom() == null
						|| (vdPlanned.getUom() != null && vdPlanned.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				MeasuredDepthCoord seaPlanned = (MeasuredDepthCoord) objWellbore1311.getMdSubSeaPlanned();

				if (seaPlanned.getUom() == null
						|| (seaPlanned.getUom() != null && seaPlanned.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				WellVerticalDepthCoord tvSeaPlanned = (WellVerticalDepthCoord) objWellbore1311.getTvdSubSeaPlanned();

				if (tvSeaPlanned.getUom() == null
						|| (tvSeaPlanned.getUom() != null && tvSeaPlanned.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				TimeMeasure dayTarget = (TimeMeasure) objWellbore1311.getDayTarget();

				if (dayTarget.getUom() == null
						|| (dayTarget.getUom() != null && dayTarget.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) {

				com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord md = (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getMd();

				if (md.getUom() == null || (md.getUom() != null && md.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvd = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getTvd();

				if (tvd.getUom() == null || (tvd.getUom() != null && tvd.getUom().toString().isEmpty())) {
					result = true;
					break;
				}
				com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord mbBit = (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getMdBit();

				if (mbBit.getUom() == null || (mbBit.getUom() != null && mbBit.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvdBit = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getTvdBit();

				if (tvdBit.getUom() == null || (tvdBit.getUom() != null && tvdBit.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord mdKickoff = (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getMdKickoff();

				if (mdKickoff.getUom() == null
						|| (mdKickoff.getUom() != null && mdKickoff.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvdKickoff = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getTvdKickoff();

				if (tvdKickoff.getUom() == null
						|| (tvdKickoff.getUom() != null && tvdKickoff.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord mdPlanned = (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getMdPlanned();

				if (mdPlanned.getUom() == null
						|| (mdPlanned.getUom() != null && mdPlanned.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvdPlanned = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getTvdPlanned();

				if (tvdPlanned.getUom() == null
						|| (tvdPlanned.getUom() != null && tvdPlanned.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord mdSubSeaPlanned = (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getMdSubSeaPlanned();

				if (mdSubSeaPlanned.getUom() == null
						|| (mdSubSeaPlanned.getUom() != null && mdSubSeaPlanned.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvdSubSeaPlanned = (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getTvdSubSeaPlanned();

				if (tvdSubSeaPlanned.getUom() == null
						|| (tvdSubSeaPlanned.getUom() != null && tvdSubSeaPlanned.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

				com.hashmapinc.tempus.WitsmlObjects.v1411.TimeMeasure dayTarget = (com.hashmapinc.tempus.WitsmlObjects.v1411.TimeMeasure) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject)
						.getDayTarget();

				if (dayTarget.getUom() == null
						|| (dayTarget.getUom() != null && dayTarget.getUom().toString().isEmpty())) {
					result = true;
					break;
				}

			}
		}

		return result;
	}

	/**
	 * This methods checks for mnemonic list for empty values.
	 * 
	 * @param XMLin
	 * @return true if mnemonic list id empty.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkMnemonicListNotEmpty(String XMLin, String WMLTypein) {
		boolean result = false;
		List<AbstractWitsmlObject> witsmlObjects;
		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			witsmlObjects = WitsmlObjectParser.parse(WMLTypein, XMLin, version);
			switch (WMLTypein) {
			case "log":
				result = checkMnemonicListforLog(witsmlObjects);
				break;
			case "trajectory":
				result = false;
				break;
			case "well":
				result = false;
				break;
			case "wellbore":
				result = false;
				break;
			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is the " + e.getMessage());
		}
		return result;
	}

	static boolean checkMnemonicListforLog(List<AbstractWitsmlObject> witsmlObjects) {
		boolean result = false;
		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjLog) {
				ObjLog objLog1311 = (ObjLog) abstractWitsmlObject;
				List<CsLogCurveInfo> logCurveInfo = objLog1311.getLogCurveInfo();
				for (CsLogCurveInfo curveInfo : logCurveInfo) {
					if (curveInfo.getMnemonic() == null
							|| curveInfo.getMnemonic() != null && curveInfo.getMnemonic().isEmpty()) {
						result = true;
						break;
					}
				}
			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) {
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> logCurveInfos = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
						.getLogCurveInfo();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo curveInfo : logCurveInfos) {
					if (curveInfo.getMnemonic() == null
							|| curveInfo.getMnemonic() != null && curveInfo.getMnemonic().toString().isEmpty()) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * This method checks for mnemonic list to be unique.
	 * 
	 * @param XMLin
	 * @return true if not unique.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkMnemonicListUnique(String XMLin, String WMLTypin) {
		boolean result = false;
		List<AbstractWitsmlObject> witsmlObjects;
		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			witsmlObjects = WitsmlObjectParser.parse(WMLTypin, XMLin, version);
			switch (WMLTypin) {
			case "log":
				result = checkMnemonicListforLog(witsmlObjects);
				break;
			case "trajectory":
				result = false;
				break;
			case "well":
				result = false;
				break;
			case "wellbore":
				result = false;
				break;
			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypin);
			}
		} catch (Exception e) {
			LOG.warning("the error is the " + e.getMessage());
		}
		return result;
	}

	static boolean checkMnemonicListUniqueforLog(List<AbstractWitsmlObject> witsmlObjects) {
		boolean result = false;
		Set<String> checkDuplicateSet = new HashSet<>();
		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjLog) {
				ObjLog objLog1311 = (ObjLog) abstractWitsmlObject;
				List<CsLogCurveInfo> logCurveInfo = objLog1311.getLogCurveInfo();
				for (CsLogCurveInfo curveInfo : logCurveInfo) {
					if (checkDuplicateSet.add(curveInfo.getMnemonic()) == false) {
						result = true;
						break;
					}
					checkDuplicateSet.add(curveInfo.getMnemonic());
				}
			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) {
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> logCurveInfos = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
						.getLogCurveInfo();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo csLogCurveInfo : logCurveInfos) {
					if (checkDuplicateSet.add(csLogCurveInfo.getMnemonic().toString()) == false) {
						result = true;
						break;
					}
					checkDuplicateSet.add(csLogCurveInfo.getMnemonic().toString());
				}
			}
		}
		return result;
	}

	/**
	 * This method checks for special characters in mnemonic list
	 * 
	 * @param XMLin
	 * @return true if special character is found.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkMnemonicForSpecialCharacters(String XMLin) {
		boolean result = false;

		try {

			String regex = "',><&//\\";

			if (!XMLin.matches(regex)) {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks for Encoding in OptionsIn
	 * 
	 * @param OptionsIn
	 * @return true if special character is found.
	 * 
	 */
	static boolean checkOptionsForEncoding(String OptionsIn) {
		boolean result = false;
		String regex = ";";
		if (!OptionsIn.matches(regex)) {
			result = true;
		}
		return result;
	}

	/**
	 * This method checks for header in OptionsIn
	 * 
	 * @param OptionsIn
	 * @return true if nested objects are not found
	 * 
	 */
	static boolean checkOptionsInHeader(String OptionsIn, String checkParam) {
		boolean result = false;
		XPath xpath = XPathFactory.newInstance().newXPath();
		InputSource inputSource = new InputSource(new StringReader(OptionsIn)); // ??? = InputStream or Reader
		try {
			if (xpath.evaluate(checkParam, inputSource).isEmpty() || xpath.evaluate(checkParam, inputSource) == null) {
				result = true;
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	static boolean checkOptions(String WMLTypein, String OptionsIn) {
		boolean result = false;

		try {

			switch (WMLTypein) {
			case "log":
				if (OptionsIn.equalsIgnoreCase("returnElements=data-only")) {
					result = true;
					break;
				}

			case "well":
				if (OptionsIn.equalsIgnoreCase("returnElements=data-only")
						|| OptionsIn.equalsIgnoreCase("returnElements=header-only")) {
					result = false;
					break;
				}
			case "wellbore":
				if (OptionsIn.equalsIgnoreCase("returnElements=data-only")
						|| OptionsIn.equalsIgnoreCase("returnElements=header-only")) {
					result = false;
					
				}
			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is " + e.getMessage());
		}
		return result;
	}

	// error 475
	static boolean checkTrajForsubUID(String XMLin, String WMLTypin) {
		boolean result = false;
		List<AbstractWitsmlObject> witsmlObjects;
		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			witsmlObjects = WitsmlObjectParser.parse(WMLTypin, XMLin, version);
			if (WMLTypin.equalsIgnoreCase("trajectory") && checkTrajUID(witsmlObjects)) {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning("the error is the " + e.getMessage());
		}
		return result;
	}

	static boolean checkTrajUID(List<AbstractWitsmlObject> witsmlObjects) {
		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjTrajectory) {
				ObjTrajectory objTraj = (ObjTrajectory) abstractWitsmlObject;
				if (objTraj.getUidWell().isEmpty() && objTraj.getUidWellbore().isEmpty()
						|| objTraj.getUidWell() == null && objTraj.getUidWellbore() == null) {
					result = true;
					break;
				}

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) {
				com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory objTraj = (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) abstractWitsmlObject;
				if (objTraj.getUidWell().isEmpty() && objTraj.getUidWellbore().isEmpty()
						|| objTraj.getUidWell() == null && objTraj.getUidWellbore() == null) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	static Validation checkErrorForAddtoStoreVersion1411() {
		return error407().and(error408()).and(error409()).and(error401()).and(error406()).and(error464())
				.and(error412()).and(error413()).and(error443()).and(error453()).and(error463());
	}

	static Validation checkErrorForAddtoStoreVersion1311() {
		return error407().and(error408()).and(error409()).and(error401()).and(error406()).and(error464())
				.and(error412()).and(error413()).and(error443()).and(error453()).and(error463());
	}

	static Validation checkErrorForGetFromStoreVersion1411() {
		return error407().and(error408()).and(error409()).and(error410()).and(error425()).and(error475())
				.and(error402()).and(error461()).and(error462()).and(error429()).and(error482());
	}

	static Validation checkErrorForGetFromStoreVersion1311() {
		return error407().and(error408()).and(error409()).and(error410()).and(error425()).and(error475())
				.and(error402()).and(error461()).and(error462()).and(error429()).and(error482());
	}

	static Validation checkErrorForUpdateInStoreVersion1411() {
		return error407().and(error408()).and(error409()).and(error464()).and(error415()).and(error401())
				.and(error443()).and(error445()).and(error464()).and(error453()).and(error463()).and(error434())
				.and(error449());
	}

	static Validation checkErrorForUpdateInStoreVersion1311() {
		return error407().and(error408()).and(error409()).and(error464()).and(error415()).and(error401())
				.and(error443()).and(error445()).and(error464()).and(error453()).and(error463()).and(error434())
				.and(error449());
	}

	static Validation checkErrorForDeleteInStoreVersion1411() {
		return error407().and(error408()).and(error414()).and(error415()).and(error416()).and(error417())
				.and(error418()).and(error419()).and(error420()).and(error437());
	}

	static Validation checkErrorForDeleteInStoreVersion1311() {
		return error407().and(error408()).and(error414()).and(error415()).and(error416()).and(error417())
				.and(error418()).and(error419()).and(error420()).and(error437());
	}
}
