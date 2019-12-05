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
package x7.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 
 * @author Sim Wang
 * 
 */
public class EventDispatcher implements EventReceiver {

	private static List<Event> eventList = new ArrayList<Event>();

	public static  List<Event> getEventTemplateList(){
		return  eventList;
	}

	private static boolean eventListenerEnabled = false;

	protected  static void enableEventListener(){
		eventListenerEnabled = true;
	}

	public static boolean isEventListenerEnabled(){
		return eventListenerEnabled;
	}

	protected static void addEventTemplate(Event event){
		eventList.add(event);
	}

	public static void addEventListener(String eventType, EventListener.Handler listener) {

		TreeMap<String, EventListener.Handler> listenerMap = listenersMap
				.get(eventType);

		if (listenerMap == null) {
			listenerMap = new TreeMap<String, EventListener.Handler>();
			listenersMap.put(eventType, listenerMap);
		} 
		String key = createKey(listener);
		if (! (listenerMap.containsKey(key))){
			listenerMap.put(key, listener);
		}

	}


	public static void removeEventListener(String eventType,
										   EventListener.Handler listener) {
		TreeMap<String, EventListener.Handler> listenerMap = listenersMap
				.get(eventType);

		if (listenerMap != null) {
			listenerMap.remove(createKey(listener));
			if (listenerMap.size() == 0) {
				listenersMap.remove(eventType);
			}
		}

	}

	public static void dispatch(Event event) {
		String key = event.getType() + event.getTag();
		TreeMap<String, EventListener.Handler> listenerMap = listenersMap
				.get(key);
		if (listenerMap == null)
			return;
		for (EventListener.Handler listener : listenerMap.values()) {
			if (listener != null) {
				try{
					listener.handle(event);
				} catch(Exception e) {
					System.out.println("\nException, while handling event: " + event + "\n");
					e.printStackTrace();
				}
			}
		}

	}
	/**
	 * CREATE KEY
	 */
	private static String createKey(EventListener.Handler listener){
		if (listener.getClass().getName().contains("EventListener.Handler")){
			return listener.getClass().getName()+listener.hashCode();
		}
		return listener.getClass().getName();
	}

}
