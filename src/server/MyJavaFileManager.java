package server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
	 
	/** La map contenant l'association nom de classe / JavaFileObject */
	private final Map<String, MyJavaFileObject> map = new HashMap<String, MyJavaFileObject>();
 
	/** Le ClassLoader qui charge les classes depuis la Map: */
	private final ClassLoader loader = new ClassLoader() {
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			MyJavaFileObject javaObject = map.get(name);
			if (javaObject == null)
				throw new ClassNotFoundException(name);
			byte[] bytes = javaObject.getByteCode();
			return defineClass(name, bytes, 0, bytes.length);
		}
	};
 
	public MyJavaFileManager(JavaFileManager manager) {
		super(manager);
	}
 
	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling) throws IOException {
		JavaFileObject javaObject = super.getJavaFileForOutput(location,
				className, kind, sibling);
		MyJavaFileObject myJavaObject = new MyJavaFileObject(javaObject);
		map.put(className, myJavaObject);
		return myJavaObject;
	}
 
	public ClassLoader getClassLoader() {
		return loader;
	}
	
}