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


import java.io.ByteArrayOutputStream;

public class IntUtil
{
	/**
	 * 进制数
	 */
	private final static char[] digits =
	{
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z', '{', '}'
	};

	/**
	 * 10进制转64进制
	 * 
	 * @param num
	 * @return
	 */
	public static String c10to64(long num)
	{
		return toUnsignedString(num, 6);
	}

	/**
	 * 10进制转32进制
	 * 
	 * @param num
	 * @return
	 */
	public static String c10to32(long num)
	{
		return toUnsignedString(num, 5);
	}

	/**
	 * 10进制转16进制
	 * 
	 * @param num
	 * @return
	 */
	public static String c10to16(long num)
	{
		return Long.toHexString(num).toUpperCase();
	}

	/**
	 * 10进制转8进制
	 * 
	 * @param num
	 * @return
	 */
	public static String c10to8(long num)
	{
		return Long.toOctalString(num);
	}

	/**
	 * 10进制转4进制
	 * 
	 * @param num
	 * @return
	 */
	public static String c10to4(long num)
	{
		return toUnsignedString(num, 2);
	}

	/**
	 * 10进制转2进制
	 * 
	 * @param num
	 * @return
	 */
	public static String c10to2(long num)
	{
		return Long.toBinaryString(num);
	}

	/**
	 * 64进制转10进制
	 * 
	 * @param num
	 * @return
	 */
	public static long c64to10(String num)
	{
		return toUnsignedLong(num, 6);
	}

	/**
	 * 32进制转10进制
	 * 
	 * @param num
	 * @return
	 */
	public static long c32to10(String num)
	{
		return toUnsignedLong(num, 5);
	}

	/**
	 * 16进制转10进制
	 * 
	 * @param num
	 * @return
	 */
	public static long c16to10(String num)
	{
		return toUnsignedLong(num, 4);
	}

	/**
	 * 8进制转10进制
	 * 
	 * @param num
	 * @return
	 */
	public static long c8to10(String num)
	{
		return toUnsignedLong(num, 3);
	}

	/**
	 * 4进制转10进制
	 * 
	 * @param num
	 * @return
	 */
	public static long c4to10(String num)
	{
		return toUnsignedLong(num, 2);
	}

	/**
	 * 2进制转10进制
	 * 
	 * @param num
	 * @return
	 */
	public static long c2to10(String num)
	{
		return toUnsignedLong(num, 1);
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////Private-Functions////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 十进制转换成X进制
	 * 
	 * @param i
	 *            long
	 * @param shift
	 *            int
	 * @return String
	 */
	private static String toUnsignedString(long i, int shift)
	{
		int bit = (int) Math.pow(2, shift);
		char[] buf = new char[bit];
		int charPos = bit;
		int radix = 1 << shift;
		long mask = radix - 1;
		do
		{
			buf[--charPos] = digits[(int) (i & mask)];
			i >>>= shift;
		}
		while (i != 0);
		return new String(buf, charPos, (bit - charPos));
	}

	private static long toUnsignedLong(String src, int shift)
	{
		int bit = (int) Math.pow(2, shift);
		long result = 0;
		int len = src.length();
		int num = len;
		int b = 0;
		String single = null;
		for (int i = 0; i < len; i++)
		{
			single = src.substring(i, i + 1);
			single = String.valueOf(getNum(single));
			b = Integer.parseInt(single);
			result = result + b * (long) Math.pow(bit, (num - 1));
			num--;
		}
		return result;
	}

	/**
	 * 获取字符对应的十进制数
	 * 
	 * @param single
	 *            String
	 * @return int
	 */
	public static int getNum(String single)
	{
		int result = 0;
		for (int n = 0; n < 64; n++)
		{
			String str = String.valueOf(digits[n]);
			if (single.equals(str))
			{
				result = n;
				break;
			}
		}
		return result;
	}

	public static long s2l(String str)
	{
		try
		{
			return Long.parseLong(str);
		}
		catch (Exception e)
		{
			return -1L;
		}
	}

	public static int s2i(String str)
	{
		try
		{
			return Integer.parseInt(str);
		}
		catch (Exception e)
		{
			return -1;
		}
	}

	public static int force_s2i(String str)
	{
		byte[] bs = str.getBytes();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean isstart = false;
		for (int i = 0; i < bs.length; i++)
		{
			if (bs[i] >= '0' && bs[i] <= '9')
			{
				isstart = true;
				baos.write(bs[i]);
			}
			else if (isstart == true)
			{
				break;
			}
		}
		return s2i(baos.toString());
	}
}