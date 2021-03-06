/*
 * Copyright (c) 2009, Joubin Houshyar <alphazero at sensesay dot net>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *     
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *     
 *   * Neither the name of JRedis nor the names of its contributors may be used
 *     to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */ 

package org.jredis.ri.alphazero.support;



import junit.framework.TestCase;

/**
 * @author Joubin Houshyar (alphazero@sensesay.net)
 * 
 */
public class ConvertTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetNaturalNumber() {
		byte[] data = null;
		
		// test null
		boolean inputChecking;
		inputChecking = false;
		try { 
			Convert.getInt(data); 
		}
		catch (IllegalArgumentException e){ inputChecking = true;}
		finally { assertTrue(inputChecking);}
		
		// test garbage data
		inputChecking = false;
		data = "2be?".getBytes();
		try { 
			Convert.getInt(data); 
		}
		catch (IllegalArgumentException e){ inputChecking = true;}
		finally { assertTrue(inputChecking);}
		
		// test big data
		// this is bigger than an bit 
		inputChecking = false;
		data = "1234567890123456".getBytes();
		try { Convert.getInt(data); }
		catch (IllegalArgumentException e){ inputChecking = true; }
		finally { assertTrue(inputChecking);}
		
		{
		// test 0
		data = "0".getBytes();
		int value = Convert.getInt(data);
		assertEquals(0, value);
		// test -0
		data = "-0".getBytes();
		// test +0
		data = "+0".getBytes();
		assertEquals(0, Convert.getInt(data));
		// test -00000
		data = "-0000".getBytes();
		assertEquals(0, Convert.getInt(data));
		// test 00000
		data = "0000".getBytes();
		// test +00000
		data = "+0000".getBytes();
		assertEquals(0, Convert.getInt(data));
		// test 00001
		data = "00001".getBytes();
		assertEquals(1, Convert.getInt(data));
		// test 0000100
		data = "0000100".getBytes();
		assertEquals(100, Convert.getInt(data));
		// test 00001
		data = "+00001".getBytes();
		assertEquals(1, Convert.getInt(data));
		// test +0000100
		data = "+0000100".getBytes();
		assertEquals(100, Convert.getInt(data));
		// test 00001
		data = "-00001".getBytes();
		assertEquals(-1, Convert.getInt(data));
		// test +0000100
		data = "-0000100".getBytes();
		assertEquals(-100, Convert.getInt(data));
		// test 1
		data = "1".getBytes();
		assertEquals(1, Convert.getInt(data));
		// test -1
		data = "-1".getBytes();
		assertEquals(-1, Convert.getInt(data));
		// test +1
		data = "+1".getBytes();
		assertEquals(1, Convert.getInt(data));
		
		// test $+1
		data = "$+1".getBytes();
		assertEquals(1, Convert.getInt(data, 1, data.length-1));
		
		// test $-1
		data = "$-1".getBytes();
		assertEquals(-1, Convert.getInt(data, 1, data.length-1));
		
		// test $+1GARBAGEDATA
		data = "$+1GARBAGEDATA".getBytes();
		assertEquals(1, Convert.getInt(data, 1, 2));
		
		// test $-1GARAGEDATA
		data = "$-1GARAGEDATA".getBytes();
		assertEquals(-1, Convert.getInt(data, 1, 2));
		
		// do a sensible range
		int java ;
		for(int i=-50000; i<50000; i++) {
			data = Convert.toBytes(i);
			java = Integer.parseInt(new String(data), 10);
			assertEquals (java, Convert.getInt(data));
		}
		
		// now lets go to the limit
		//
		data = Integer.toString(Integer.MAX_VALUE).getBytes();
		assertEquals(Integer.MAX_VALUE, Convert.getInt(data, 0, data.length));

		data = Integer.toString(Integer.MIN_VALUE).getBytes();
		assertEquals(Integer.MIN_VALUE, Convert.getInt(data, 0, data.length));
		
		}
		
		// now lets do the longs

		{
		// test big data
		// this is bigger than an bit  but fine for a long
		inputChecking = false;
		data = "1234567890123456".getBytes();
		assertEquals (1234567890123456L, Convert.getLong(data));
		
		// test 0
		data = "0".getBytes();
		long value = Convert.getLong(data);
		assertEquals(0, value);
		// test -0
		data = "-0".getBytes();
		// test +0
		data = "+0".getBytes();
		assertEquals(0, Convert.getLong(data));
		// test -00000
		data = "-0000".getBytes();
		assertEquals(0, Convert.getLong(data));
		// test 00000
		data = "0000".getBytes();
		// test +00000
		data = "+0000".getBytes();
		assertEquals(0, Convert.getLong(data));
		// test 00001
		data = "00001".getBytes();
		assertEquals(1, Convert.getLong(data));
		// test 0000100
		data = "0000100".getBytes();
		assertEquals(100, Convert.getLong(data));
		// test 00001
		data = "+00001".getBytes();
		assertEquals(1, Convert.getLong(data));
		// test +0000100
		data = "+0000100".getBytes();
		assertEquals(100, Convert.getLong(data));
		// test 00001
		data = "-00001".getBytes();
		assertEquals(-1, Convert.getLong(data));
		// test +0000100
		data = "-0000100".getBytes();
		assertEquals(-100, Convert.getLong(data));
		// test 1
		data = "1".getBytes();
		assertEquals(1, Convert.getLong(data));
		// test -1
		data = "-1".getBytes();
		assertEquals(-1, Convert.getLong(data));
		// test +1
		data = "+1".getBytes();
		assertEquals(1, Convert.getLong(data));
		
		// test $+1
		data = "$+1".getBytes();
		assertEquals(1, Convert.getLong(data, 1, data.length-1));
		
		// test $-1
		data = "$-1".getBytes();
		assertEquals(-1, Convert.getLong(data, 1, data.length-1));
		
		// test $+1GARBAGEDATA
		data = "$+1GARBAGEDATA".getBytes();
		assertEquals(1, Convert.getLong(data, 1, 2));
		
		// test $-1GARAGEDATA
		data = "$-1GARAGEDATA".getBytes();
		assertEquals(-1, Convert.getLong(data, 1, 2));
		
		// now lets go to the limit
		//
		data = Long.toString(Long.MAX_VALUE).getBytes();
		assertEquals(Long.MAX_VALUE, Convert.getLong(data, 0, data.length));

		data = Long.toString(Long.MIN_VALUE).getBytes();
		assertEquals(Long.MIN_VALUE, Convert.getLong(data, 0, data.length));
		
		}  // just for scoping  ..
	}
	/**
	 * Test method for {@link org.jredis.alphazero.util.util.S27.jredis_deprecated.client.util.Convert#toBytes(int, boolean)}.
	 */
	public void testToBytes() {
		byte[] javadata = null;
		byte[] data = null;

		// test MIN 
		int n;
		n=Integer.MIN_VALUE;
		javadata = Integer.toString(n).getBytes();
		data = Convert.toBytes(n);
		assertEquals ("buffer length", javadata.length, data.length);
		for(int j=0; j<data.length;j++)
			assertEquals("for <"+n+"> byte @ ["+j+"]", javadata[j], data[j]);

		// test MAX
		n=Integer.MAX_VALUE;
		javadata = Integer.toString(n).getBytes();
		data = Convert.toBytes(n);
		assertEquals ("buffer length", javadata.length, data.length);
		for(int j=0; j<data.length;j++)
			assertEquals("for <"+n+"> byte @ ["+j+"]", javadata[j], data[j]);

		// test a bit smaller than Convert.INT_N_65535
		n=Convert.INT_N_65535 - 444;
		javadata = Integer.toString(n).getBytes();
		data = Convert.toBytes(n);
		assertEquals ("buffer length", javadata.length, data.length);
		for(int j=0; j<data.length;j++)
			assertEquals("for <"+n+"> byte @ ["+j+"]", javadata[j], data[j]);

		// test a bit larger than Convert.INT_P_65535
		n=Convert.INT_P_65535 + 444;
		javadata = Integer.toString(n).getBytes();
		data = Convert.toBytes(n);
		assertEquals ("buffer length", javadata.length, data.length);
		for(int j=0; j<data.length;j++)
			assertEquals("for <"+n+"> byte @ ["+j+"]", javadata[j], data[j]);

		// test the exact range range
		for(int i=Convert.INT_N_65535; i<Convert.INT_P_65535; i++){
			//		for(int i=Integer.MIN_VALUE; i<Integer.MAX_VALUE; i++){
			javadata = Integer.toString(i).getBytes();
			data = Convert.toBytes(i);
			assertEquals ("buffer length", javadata.length, data.length);
			for(int j=0; j<data.length;j++)
				assertEquals("for <"+i+"> byte @ ["+j+"]", javadata[j], data[j]);
		}		
	}
}
