package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.tools.ForwardingJavaFileObject;
import javax.tools.JavaFileObject;

class MyJavaFileObject extends ForwardingJavaFileObject<JavaFileObject> {
	 
	private ByteArrayOutputStream data;
 
	public MyJavaFileObject(JavaFileObject object) {
		super(object);
	}
 
	@Override
	public OutputStream openOutputStream() throws IOException {
		this.data = new ByteArrayOutputStream();
		return data;
	}
 
	public byte[] getByteCode() {
		return data.toByteArray(); // NPE si jamais ouvert
	}
}