/*
 *   Copyright 2009 Joubin Houshyar
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *    
 *   http://www.apache.org/licenses/LICENSE-2.0
 *    
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jredis.ri.alphazero;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jredis.ClientRuntimeException;
import org.jredis.Command;
import org.jredis.JRedis;
import org.jredis.ProviderException;
import org.jredis.RedisException;
import org.jredis.RedisType;
import org.jredis.Sort;
import org.jredis.connector.BulkResponse;
import org.jredis.connector.Connection;
import org.jredis.connector.MultiBulkResponse;
import org.jredis.connector.Response;
import org.jredis.connector.ValueResponse;
import org.jredis.ri.alphazero.support.Convert;
import org.jredis.ri.alphazero.support.DefaultCodec;
import org.jredis.ri.alphazero.support.SortSupport;

/**
 * 
 * [TODO: document me!]
 *
 * @author  Joubin Houshyar (alphazero@sensesay.net)
 * @version alpha.0, Apr 10, 2009
 * @since   alpha.0
 *
 */
public abstract class JRedisSupport implements JRedis {
	
	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
//	/** No setter or getters for this property - it is initialized at construct time. */
//	private Connection	connection;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------
//	/**
//	 * @param connection
//	 */
//	protected final void setConnection (Connection connection) {
//		this.connection = Assert.notNull(connection, "connection on setConnection()", ClientRuntimeException.class);
//	}
//	/**
//	 * @return
//	 */
//	protected final Connection getConnection () {
//		return this.connection;
//	}
	
	// ------------------------------------------------------------------------
	// Extension point(s)
	/*
	 * This class provides the convenience of a uniform implementation wide mapping
	 * of JRedis api semantics to the native protocol level semantics of byte[]s.
	 * 
	 * Extensions can use the provided extension points to provide or delegate the
	 * servicing of request calls.  
	 */
	// ------------------------------------------------------------------------
	
	/**
	 * This method mimics the eponymous {@link Connection#serviceRequest(Command, byte[]...)}
	 * which defines the blocking api semantics of Synchronous connections.  The extending class
	 * can either directly (a) implement the protocol requirements, or, (b) delegate to a
	 * {@link Connection} instance, or, (c) utilize a pool of {@link Connection}s.  
	 * 
	 * @param cmd
	 * @param args
	 * @return
	 * @throws RedisException
	 * @throws ClientRuntimeException
	 * @throws ProviderException
	 */
	protected abstract Response serviceRequest (Command cmd, byte[]...args) throws RedisException, ClientRuntimeException, ProviderException; 
	// ------------------------------------------------------------------------
	// INTERFACE
	// ================================================================ Redis
	/*
	 * Support of all the JRedis interface methods.
	 * 
	 * This class uses the UTF-8 character set for all conversions due to its
	 * use of the Convert and Codec support classes.
	 * 
	 * All calls are forwarded to an abstract serviceRequest method that the
	 * extending classes are expected to implement.  
	 * 
	 * Implementation note:
	 * The methods in this class use redundant code in marshalling request params
	 * and in unmarshalling the response data.  We certainly can use a few helper
	 * functions to reduce the redundancy, but given that such methods would be
	 * repeatedly called, it was decided to effectively inline these statements in 
	 * each method body.  
	 */
	// ------------------------------------------------------------------------


//	@Override
	public JRedis auth(String key) throws RedisException {
		byte[] keydata = null;
		if((keydata = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		this.serviceRequest(Command.AUTH, keydata);
		return this;
	}
//	@Override
	public void bgsave() throws RedisException {
		this.serviceRequest(Command.BGSAVE);
	}
//	@Override
	public JRedis ping() throws RedisException {
		this.serviceRequest(Command.PING);
		return this;
	}

//	@Override
	public JRedis flushall() throws RedisException {
		this.serviceRequest(Command.FLUSHALL).getStatus();
		return this;
	}
//	@Override
	public JRedis flushdb() throws RedisException {
		this.serviceRequest(Command.FLUSHDB).getStatus();
		return this;
	}
//	@Override
	public JRedis select(int index) throws RedisException {
		this.serviceRequest(Command.SELECT, Convert.toBytes(index));
		return this;
	}
//	@Override
	public void rename(String oldkey, String newkey) throws RedisException {
		byte[] oldkeydata = null;
		if((oldkeydata = getKeyBytes(oldkey)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+oldkey+"]");

		byte[] newkeydata = null;
		if((newkeydata = getKeyBytes(newkey)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+newkey+"]");

		this.serviceRequest(Command.RENAME, oldkeydata, newkeydata);
	}
	
//	@Override
	public boolean renamenx(String oldkey, String newkey) throws RedisException{
		byte[] oldkeydata = null;
		if((oldkeydata = getKeyBytes(oldkey)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+oldkey+"]");

		byte[] newkeydata = null;
		if((newkeydata = getKeyBytes(newkey)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+newkey+"]");

		/* boolean ValueRespose */
		boolean value = false;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.RENAMENX, oldkeydata, newkeydata);
			value = valResponse.getBooleanValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return value;
	}
	
//	@Override
	public void rpush(String key, byte[] value) 
	throws RedisException 
	{
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");
		
		this.serviceRequest(Command.RPUSH, keybytes, value);
	}
//	@Override
	public void rpush(String key, String value) throws RedisException {
//		rpush(key, DefaultCodec.encode(value));
		rpush(key, DefaultCodec.encode(value));
	}
//	@Override
	public void rpush(String key, Number value) throws RedisException {
		rpush(key, String.valueOf(value).getBytes());
	}
//	@Override
	public <T extends Serializable> void rpush (String key, T value) throws RedisException
	{
		rpush(key, DefaultCodec.encode(value));
	}

//	@Override
	public boolean sadd(String key, byte[] member) 
	throws RedisException 
	{
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");
		/* boolean ValueRespose */
		boolean res = false;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.SADD, keybytes, member);
			res = valResponse.getBooleanValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return res;
	}
//	@Override
	public boolean sadd (String key, String value) throws RedisException {
		return sadd (key, DefaultCodec.encode(value));
	}
//	@Override
	public boolean sadd (String key, Number value) throws RedisException {
		return sadd (key, String.valueOf(value).getBytes());
	}
//	@Override
	public <T extends Serializable> boolean sadd (String key, T value) throws RedisException
	{
		return sadd (key, DefaultCodec.encode(value));
	}

//	@Override
	public void save() 
	throws RedisException 
	{
		this.serviceRequest(Command.SAVE);
	}
	
	// -------- set 

//	@Override
	public void set(String key, byte[] value) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		this.serviceRequest(Command.SET, keybytes, value);
	}
//	@Override
	public void set(String key, String value) throws RedisException {
		set(key, DefaultCodec.encode(value));
	}
//	@Override
	public void set(String key, Number value) throws RedisException {
		set(key, String.valueOf(value).getBytes());
	}
//	@Override
	public <T extends Serializable> void set (String key, T value) throws RedisException
	{
		set(key, DefaultCodec.encode(value));
	}
	
//	@Override
	public byte[] getset(String key, byte[] value) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		byte[] bulkData= null;
		try {
			BulkResponse response = (BulkResponse) this.serviceRequest(Command.GETSET, keybytes, value);
			bulkData = response.getBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a BulkResponse here => " + e.getLocalizedMessage(), e);
		}
		return bulkData;
	}
//	@Override
	public byte[] getset(String key, String value) throws RedisException {
		return getset(key, DefaultCodec.encode(value));
	}
//	@Override
	public byte[] getset(String key, Number value) throws RedisException {
		return getset(key, String.valueOf(value).getBytes());
	}
//	@Override
	public <T extends Serializable> 
	byte[] getset (String key, T value) throws RedisException
	{
		return getset(key, DefaultCodec.encode(value));
	}
	
//	@Override
	public boolean setnx(String key, byte[] value) throws RedisException{
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		boolean resvalue = false;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.SETNX, keybytes, value);
			resvalue = valResponse.getBooleanValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return resvalue;
	}
//	@Override
	public boolean setnx(String key, String value) throws RedisException {
		return setnx(key, DefaultCodec.encode(value));
	}
//	@Override
	public boolean setnx(String key, Number value) throws RedisException {
		return setnx(key, String.valueOf(value).getBytes());
	}
//	@Override
	public <T extends Serializable> boolean setnx (String key, T value) throws RedisException {
		return setnx(key, DefaultCodec.encode(value));
	}

//	@Override
	public boolean sismember(String key, byte[] member) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		/* boolean ValueRespose */
		boolean value = false;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.SISMEMBER, keybytes, member);
			value = valResponse.getBooleanValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return value;
	}

//	@Override
	public boolean sismember(String key, String value) throws RedisException {
		return sismember(key, DefaultCodec.encode(value));
	}

//	@Override
	public boolean sismember(String key, Number numberValue) throws RedisException {
		return sismember (key, String.valueOf(numberValue).getBytes());
	}

//	@Override
	public <T extends Serializable> boolean sismember(String key, T object) throws RedisException {
		return sismember(key, DefaultCodec.encode(object));
	}

	public boolean smove (String srcKey, String destKey, byte[] member) throws RedisException {
		byte[] srcKeyBytes = null;
		if((srcKeyBytes = getKeyBytes(srcKey)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+srcKey+"]");

		byte[] destKeyBytes = null;
		if((destKeyBytes = getKeyBytes(destKey)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+destKey+"]");

		/* boolean ValueRespose */
		boolean value = false;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.SMOVE, srcKeyBytes, destKeyBytes, member);
			value = valResponse.getBooleanValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return value;
	}
	public boolean smove (String srcKey, String destKey, String stringValue) throws RedisException {
		return smove (srcKey, destKey, DefaultCodec.encode(stringValue));
	}
	public boolean smove (String srcKey, String destKey, Number numberValue) throws RedisException {
		return smove (srcKey, destKey, String.valueOf(numberValue).getBytes());
	}
	public <T extends Serializable> 
		   boolean smove (String srcKey, String destKey, T object) throws RedisException {
		return smove (srcKey, destKey, DefaultCodec.encode(object));
	}
		   
	
	
	/* ------------------------------- commands returning int value --------- */

//	@Override
	public long incr(String key) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		/* ValueRespose */
		long value = Long.MIN_VALUE;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.INCR, keybytes);
			value = valResponse.getLongValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return value;
	}

//	@Override
	public long incrby(String key, int delta) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		/* ValueRespose */
		long value = Long.MIN_VALUE;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.INCRBY, keybytes, Convert.toBytes(delta));
			value = valResponse.getLongValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return value;
	}

//	@Override
	public long decr(String key) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		/* ValueRespose */
		long value = Long.MAX_VALUE;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.DECR, keybytes);
			value = valResponse.getLongValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return value;
	}

//	@Override
	public long decrby(String key, int delta) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		/* ValueRespose */
		long value = Long.MAX_VALUE;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.DECRBY, keybytes, Convert.toBytes(delta));
			value = valResponse.getLongValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return value;
	}

//	@Override
	public long llen(String key) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		/* ValueRespose */ /* int since max size is 1GB, an integer 1,073,741,824 */
		long value = Integer.MIN_VALUE;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.LLEN, keybytes);
			value = valResponse.getLongValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return value;
	}

//	@Override
	public long scard(String key) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");
		long value = Long.MIN_VALUE;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.SCARD, keybytes);
			value = valResponse.getLongValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return value;
	}

	/* ------------------------------- commands returning long value --------- */

//	@Override
	public long dbsize() throws RedisException {
		long value = Long.MIN_VALUE;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.DBSIZE);
			value = valResponse.getLongValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return value;
	}
//	@Override
	public long lastsave() throws RedisException {
		long value = Long.MIN_VALUE;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.LASTSAVE);
			value = valResponse.getLongValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return value;
	}

	/* ------------------------------- commands returning byte[] --------- */

//	@Override
	public byte[] get(String key) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		byte[] bulkData= null;
		try {
			BulkResponse response = (BulkResponse) this.serviceRequest(Command.GET, keybytes);
			bulkData = response.getBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a BulkResponse here => " + e.getLocalizedMessage(), e);
		}
		return bulkData;
	}

//	@Override
	public byte[] lindex(String key, long index) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		byte[] bulkData= null;
		try {
			BulkResponse response = (BulkResponse) this.serviceRequest(Command.LINDEX, keybytes, Convert.toBytes(index));
			bulkData = response.getBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a BulkResponse here => " + e.getLocalizedMessage(), e);
		}
		return bulkData;
	}
//	@Override
	public byte[] lpop(String key) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		byte[] bulkData= null;
		try {
			BulkResponse response = (BulkResponse) this.serviceRequest(Command.LPOP, keybytes);
			bulkData = response.getBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a BulkResponse here => " + e.getLocalizedMessage(), e);
		}
		return bulkData;
	}

//	@Override
	public byte[] rpop(String key) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		byte[] bulkData= null;
		try {
			BulkResponse response = (BulkResponse) this.serviceRequest(Command.RPOP, keybytes);
			bulkData = response.getBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a BulkResponse here => " + e.getLocalizedMessage(), e);
		}
		return bulkData;
	}


	/* ------------------------------- commands returning String--------- */

//	@Override
	public String randomkey() throws RedisException {
		/* ValueRespose */
		String stringValue = null;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.RANDOMKEY);
			stringValue = valResponse.getStringValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return stringValue;
	}
//	@Override
	public RedisType type(String key) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		RedisType	type = null;
		/* ValueRespose */
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.TYPE, keybytes);
			String stringValue = valResponse.getStringValue();
			type = RedisType.valueOf(stringValue);
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return type;
	}

	/* ------------------------------- commands returning Maps --------- */

//	@Override
	public Map<String, String> info() throws RedisException {

		byte[] bulkData= null;
		try {
			BulkResponse response = (BulkResponse) this.serviceRequest(Command.INFO);
			bulkData = response.getBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a BulkResponse here => " + e.getLocalizedMessage(), e);
		}

		StringTokenizer tokenizer = new StringTokenizer(new String(bulkData), "\r\n");
		Map<String, String>  infomap = new HashMap<String, String>(12);
		while (tokenizer.hasMoreTokens()){
			String info = tokenizer.nextToken();
			int c = info.indexOf(':');
			String key =info.substring(0, c);
			String value = info.substring(c+1);
			infomap.put(key, value);
		}
		return infomap;
	}

	/* ------------------------------- commands returning Lists --------- */

//	@Override
	public List<byte[]> mget(String key, String... moreKeys) throws RedisException {

		byte[] keydata = null;
		if((keydata = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		byte[][] keybytes = new byte[1+moreKeys.length][];
		int i=0; keybytes[i++] = keydata;
		for(String k : moreKeys) {
			if((keydata = getKeyBytes(k)) == null) 
				throw new IllegalArgumentException ("invalid key => ["+k+"]");
			keybytes[i++] = keydata;
		}
		
		List<byte[]> multiBulkData= null;
		try {
			MultiBulkResponse MultiBulkResponse = (MultiBulkResponse) this.serviceRequest(Command.MGET, keybytes);
			multiBulkData = MultiBulkResponse.getMultiBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a MultiBulkResponse here => " + e.getLocalizedMessage(), e);
		}
		return multiBulkData;
	}

//	@Override
	public List<byte[]> smembers(String key) throws RedisException {
		byte[] keydata = null;
		if((keydata = getKeyBytes(key)) == null) 
			throw new RedisException (Command.KEYS, "ERR Invalid key.");

		List<byte[]> multiBulkData= null;
		try {
			MultiBulkResponse MultiBulkResponse = (MultiBulkResponse) this.serviceRequest(Command.SMEMBERS, keydata);
			multiBulkData = MultiBulkResponse.getMultiBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a MultiBulkResponse here => " + e.getLocalizedMessage(), e);
		}
		return multiBulkData;
	}
//	@Override
	public List<String> keys() throws RedisException {
		return this.keys("*");
	}

//	@Override
	public List<String> keys(String pattern) throws RedisException {
		byte[] keydata = null;
		if((keydata = getKeyBytes(pattern)) == null) 
			throw new RedisException (Command.KEYS, "ERR Invalid key.");


		byte[] bulkData= null;
		try {
			BulkResponse response = (BulkResponse) this.serviceRequest(Command.KEYS, keydata);
			bulkData = response.getBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a BulkResponse here => " + e.getLocalizedMessage(), e);
		}

		StringTokenizer tokenizer = new StringTokenizer(new String(bulkData), " ");
		List<String>  keyList = new ArrayList <String>(12);
		while (tokenizer.hasMoreTokens()){
			keyList.add(tokenizer.nextToken());
		}
		return keyList;
	}

//	@Override
	public List<byte[]> lrange(String key, long from, long to) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		byte[] fromBytes = Convert.toBytes(from);
		byte[] toBytes = Convert.toBytes(to);

		List<byte[]> multiBulkData= null;
		try {
			MultiBulkResponse MultiBulkResponse = (MultiBulkResponse) this.serviceRequest(Command.LRANGE, keybytes, fromBytes, toBytes);
			multiBulkData = MultiBulkResponse.getMultiBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a MultiBulkResponse here => " + e.getLocalizedMessage(), e);
		}
		return multiBulkData;
	}

	// TODO: NOTIMPLEMENTED:
//	@Override
	public Sort sort(final String key) {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		final JRedisSupport client = this;
		Sort sortQuery = new SortSupport (key, keybytes) {
		//	@Override 
			protected List<byte[]> execSort(byte[] keyBytes, byte[] sortSpecBytes) 
			throws IllegalStateException, RedisException {
				
				List<byte[]> multiBulkData= null;
				try {
					MultiBulkResponse MultiBulkResponse = (MultiBulkResponse) client.serviceRequest(Command.SORT, keyBytes, sortSpecBytes);
					multiBulkData = MultiBulkResponse.getMultiBulkData();
				}
				catch (ClassCastException e){
					throw new ProviderException("Expecting a MultiBulkResponse here => " + e.getLocalizedMessage(), e);
				}
				return multiBulkData;
			}
		};
		return sortQuery;
	}

	/* ------------------------------- commands that don't get a response --------- */

//	@Override
	public void quit()  {
		try {
			this.serviceRequest(Command.QUIT);
		}
		catch (RedisException e) { /* NotConnectedException is OK */
			e.printStackTrace();
			throw new ProviderException ("Quit raised an unexpected RedisException -- Bug");
		}
//		return true;
	}
//	@Override
	public void shutdown() {
		try {
			this.serviceRequest(Command.SHUTDOWN);
		}
		catch (RedisException e) { /* NotConnectedException is OK */
			e.printStackTrace();
			throw new ProviderException ("Shutdown raised an unexpected RedisException -- Bug");
		}
//		return true;
	}
//	@Override
	public List<byte[]> sinter(String set1, String... sets) throws RedisException {
		byte[] keydata = null;
		if((keydata = getKeyBytes(set1)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+set1+"]");

		byte[][] keybytes = new byte[1+sets.length][];
		int i=0; keybytes[i++] = keydata;
		for(String k : sets) {
			if((keydata = getKeyBytes(k)) == null) 
				throw new IllegalArgumentException ("invalid key => ["+k+"]");
			keybytes[i++] = keydata;
		}
		
		List<byte[]> multiBulkData= null;
		try {
			MultiBulkResponse MultiBulkResponse = (MultiBulkResponse) this.serviceRequest(Command.SINTER, keybytes);
			multiBulkData = MultiBulkResponse.getMultiBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a MultiBulkResponse here => " + e.getLocalizedMessage(), e);
		}
		return multiBulkData;
	}

//	@Override
	public List<byte[]> sunion(String set1, String... sets) throws RedisException {
		byte[] keydata = null;
		if((keydata = getKeyBytes(set1)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+set1+"]");

		byte[][] keybytes = new byte[1+sets.length][];
		int i=0; keybytes[i++] = keydata;
		for(String k : sets) {
			if((keydata = getKeyBytes(k)) == null) 
				throw new IllegalArgumentException ("invalid key => ["+k+"]");
			keybytes[i++] = keydata;
		}
		
		List<byte[]> multiBulkData= null;
		try {
			MultiBulkResponse MultiBulkResponse = (MultiBulkResponse) this.serviceRequest(Command.SUNION, keybytes);
			multiBulkData = MultiBulkResponse.getMultiBulkData();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a MultiBulkResponse here => " + e.getLocalizedMessage(), e);
		}
		return multiBulkData;
	}

//	@Override
	public void sinterstore(String dest, String... sets) throws RedisException {
		byte[] keydata = null;
		if((keydata = getKeyBytes(dest)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+dest+"]");

		byte[][] setbytes = new byte[1+sets.length][];
		int i=0; 
		setbytes[i++] = keydata;
		byte[] setdata =null;
		for(String k : sets) {
			if((setdata = getKeyBytes(k)) == null) 
				throw new IllegalArgumentException ("invalid key => ["+k+"]");
			setbytes[i++] = setdata;
		}
		
		this.serviceRequest(Command.SINTERSTORE, setbytes);
	}

//	@Override
	public void sunionstore(String dest, String... sets) throws RedisException {
		byte[] keydata = null;
		if((keydata = getKeyBytes(dest)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+dest+"]");

		byte[][] setbytes = new byte[1+sets.length][];
		int i=0; 
		setbytes[i++] = keydata;
		byte[] setdata =null;
		for(String k : sets) {
			if((setdata = getKeyBytes(k)) == null) 
				throw new IllegalArgumentException ("invalid key => ["+k+"]");
			setbytes[i++] = setdata;
		}
		
		this.serviceRequest(Command.SUNIONSTORE, setbytes);
	}

//	@Override
	public boolean del(String key) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		boolean resvalue = false;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.DEL, keybytes);
			resvalue = valResponse.getBooleanValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return resvalue;
	}


//	@Override
	public boolean exists(String key) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		boolean resvalue = false;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.EXISTS, keybytes);
			resvalue = valResponse.getBooleanValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return resvalue;
	}


//	@Override
	public void lpush(String key, byte[] value) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");
		
		this.serviceRequest(Command.LPUSH, keybytes, value);
	}
//	@Override
	public void lpush(String key, String value) throws RedisException {
		lpush(key, DefaultCodec.encode(value));
	}
//	@Override
	public void lpush(String key, Number value) throws RedisException {
		lpush(key, String.valueOf(value).getBytes());
	}
//	@Override
	public <T extends Serializable> void lpush (String key, T value) throws RedisException
	{
		lpush(key, DefaultCodec.encode(value));
	}
	


//	@Override
	public long lrem(String key, byte[] value, int count) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		byte[] countBytes = Convert.toBytes(count);

		long remcnt = 0;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.LREM, keybytes, value, countBytes);
			remcnt = valResponse.getLongValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return remcnt;
	}
//	@Override
	public long lrem (String listKey, String value, int count) throws RedisException{
		return lrem (listKey, DefaultCodec.encode(value), count);
	}
//	@Override
	public long lrem (String listKey, Number numberValue, int count) throws RedisException {
		return lrem (listKey, String.valueOf(numberValue).getBytes(), count);
	}
//	@Override
	public <T extends Serializable> 
	long lrem (String listKey, T object, int count) throws RedisException{
		return lrem (listKey, DefaultCodec.encode(object), count);
	}


//	@Override
	public void lset(String key, long index, byte[] value) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		byte[] indexBytes = Convert.toBytes(index);
		this.serviceRequest(Command.LSET, keybytes, indexBytes, value);
	}
//	@Override
	public void lset (String key, long index, String value) throws RedisException {
		lset (key, index, DefaultCodec.encode(value));
	}
//	@Override
	public void lset (String key, long index, Number numberValue) throws RedisException{
		lset (key, index, String.valueOf(numberValue).getBytes());
	}
//	@Override
	public <T extends Serializable> void lset (String key, long index, T object) throws RedisException{
		lset (key, index, DefaultCodec.encode(object));
	}

//	@Override
	public boolean move(String key, int dbIndex) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");
		
		byte[] toBytes = Convert.toBytes(dbIndex);

		boolean resvalue = false;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.MOVE, keybytes, toBytes);
			resvalue = valResponse.getBooleanValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return resvalue;
	}


//	@Override
	public boolean srem(String key, byte[] member) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		boolean resvalue = false;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.SREM, keybytes, member);
			resvalue = valResponse.getBooleanValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return resvalue;
	}
//	@Override
	public boolean srem (String key, String value) throws RedisException {
		return srem (key, DefaultCodec.encode(value));
	}
//	@Override
	public boolean srem (String key, Number value) throws RedisException {
		return srem (key, String.valueOf(value).getBytes());
	}
//	@Override
	public <T extends Serializable> boolean srem (String key, T value) throws RedisException
	{
		return srem (key, DefaultCodec.encode(value));
	}


//	@Override
	public void ltrim(String key, long keepFrom, long keepTo) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		byte[] fromBytes = Convert.toBytes(keepFrom);
		byte[] toBytes = Convert.toBytes(keepTo);
		this.serviceRequest(Command.LTRIM, keybytes, fromBytes, toBytes);
	}

//	@Override
	public boolean expire(String key, int ttlseconds) throws RedisException {
		byte[] keybytes = null;
		if((keybytes = getKeyBytes(key)) == null) 
			throw new IllegalArgumentException ("invalid key => ["+key+"]");

		byte[] ttlbytes = Convert.toBytes(ttlseconds);
		
		boolean resvalue = false;
		try {
			ValueResponse valResponse = (ValueResponse) this.serviceRequest(Command.EXPIRE, keybytes, ttlbytes);
			resvalue = valResponse.getBooleanValue();
		}
		catch (ClassCastException e){
			throw new ProviderException("Expecting a ValueResponse here => " + e.getLocalizedMessage(), e);
		}
		return resvalue;
	}

	// TODO: integrate using KeyCodec and a CodecManager at client spec and init time.
	// this isn't cooked yet -- lets think more about the implications...
	// 
	static final private Map<String, byte[]>	keyByteCache = new HashMap<String, byte[]>();
	//public static final boolean	CacheKeys	= true;
	public static final boolean	CacheKeys	= false;

	private byte[] getKeyBytes(String key) throws IllegalArgumentException {
		if(null == key) throw new IllegalArgumentException("key is null");
		byte[] bytes = null;
		if(JRedisSupport.CacheKeys == true)
			bytes = keyByteCache.get(key);
		if(null == bytes) {
			bytes = key.getBytes(DefaultCodec.SUPPORTED_CHARSET);
			for(byte b : bytes) {
				if (b == (byte)32 || b == (byte)10 || b == (byte)13)
					throw new IllegalArgumentException ("Key includes invalid byte value: " + (int)b);
			}
			
			if(JRedisSupport.CacheKeys == true)
				keyByteCache.put(key, bytes);
		}

		return bytes;
	}
}
