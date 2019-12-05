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
package x7.core.web;

import x7.core.util.JsonX;

/**
 * 
 * Simple third part API PROTOCAL
 * 
 * @author Sim
 *
 */
public interface RestAPI {
	
	String TOAST_SIGN_EXCEPTION = "SIGN UNAVAILABLE";
	
	interface Signed {
		String sign ();
		long getTime();
		String getId();
	}
	
	class SignException extends RuntimeException{
		
		private String message;
		public SignException(String message){
			this.message = message;
		}
		public SignException(){
		}
		public String getMessage() {
			return message;
		}
	}
	
	class RequestMessage<T> {
		
		private T body;
		private String id;
		private long time;
		private String sign;
		
		public boolean isSigned(Signed signed) {
			return sign.equals(signed.sign());
		}
		
		public T getBody() {
			return body;
		}
		public void setBody(T body) {
			this.body = body;
		}
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public long getTime() {
			return time;
		}
		public void setTime(long time) {
			this.time = time;
		}
		public String getSign() {
			return sign;
		}
		public void setSign(String sign) {
			this.sign = sign;
		}
		@Override
		public String toString() {
			return "RequestMessage [body=" + body + ", id=" + id + ", time=" + time + ", sign=" + sign + "]";
		}
	}
	
	class Callback {
		
		public static Message parse (String responseStr, Signed signed){
			
			Message result = JsonX.toObject(responseStr, Message.class);
			if (result.getStatus().equals(ViewStatus.FAIL))
				return result;
			
			if (! result.getSign().equals(signed.sign()))
				throw new SignException(TOAST_SIGN_EXCEPTION);
			
			return result;
		}
		
	}
	
	class SendMessage {
		public static Message toast(String str) {
			Message message = new Message();
			message.setStatus(ViewStatus.FAIL);
			message.setBody(str);
			return message;
		}

		public static Message ok(Object obj, Signed signed) {

			Message message = new Message();
			message.setStatus(ViewStatus.OK);
			message.setBody(obj);
			message.setId(signed.getId());
			message.setTime(signed.getTime());
			message.setSign(signed.sign());
			return message;

		}
	}

	class Message<T> {

		private ViewStatus status;
		private T body;
		private String id;
		private long time;
		private String sign;

		public ViewStatus getStatus() {
			return status;
		}

		public void setStatus(ViewStatus status) {
			this.status = status;
		}

		public T getBody() {
			return body;
		}

		public void setBody(T body) {
			this.body = body;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public String getSign() {
			return sign;
		}

		public void setSign(String sign) {
			this.sign = sign;
		}

		@Override
		public String toString() {
			return "Message [status=" + status + ", body=" + body + ", id=" + id
					+ ", time=" + time + ", sign=" + sign + "]";
		}

	}
}
