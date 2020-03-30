/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.xream.x7.common.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class VerifyUtil {
	
	private VerifyUtil(){}
	
	public static String getSign(List<String> list){
		StringBuffer sb = new StringBuffer("");
		for(String str : list){
			sb.append(str);
		}
		String signString = VerifyUtil.toMD5(sb.toString());
		return signString;
	}
	
	
	
	/**  
	 * MD5加密类  
	 * @param str 要加密的字符串  
	 * @return    加密后的字符串  
	 */  
	public static String toMD5(String str){   
	    try {   
	        MessageDigest md = MessageDigest.getInstance("MD5");   
	        md.update(str.getBytes());   
	        byte[] byteDigest = md.digest();   
	        StringBuffer buf = new StringBuffer("");  
	        int i = 0;
	        for (int offset = 0; offset < byteDigest.length; offset++) {   
	            i = byteDigest[offset];   
	            if (i < 0)   
	               i += 256;
	           if (i < 16)   
	               buf.append("0");   
	           buf.append(Integer.toHexString(i));   
	        }
	        return buf.toString();
	        //return buf.toString().substring(8, 24);    
	    } catch (NoSuchAlgorithmException e) {   
	        e.printStackTrace();
	    }
		return null;
	}  
	
    public final static String toMD5_Char(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};       

        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}

}
