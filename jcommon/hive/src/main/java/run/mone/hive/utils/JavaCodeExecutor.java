
package run.mone.hive.utils;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;

public class JavaCodeExecutor {

    public static String execute(String code, String testCode) {
        StringBuilder result = new StringBuilder();
        
        try {
            // Compile the main code
            Class<?> compiledClass = compileAndLoadClass(code, "DynamicClass");
            result.append("Main code compiled successfully.\n");

            // Execute the main code
            result.append(executeCompiledClass(compiledClass));

            // Compile and execute test code if provided
            if (testCode != null && !testCode.isEmpty()) {
                Class<?> testClass = compileAndLoadClass(testCode, "DynamicTestClass");
                result.append("\nTest code compiled successfully.\n");
                result.append(executeCompiledClass(testClass));
            }
        } catch (Exception e) {
            result.append("Execution failed: ").append(e.getMessage());
        }

        return result.toString();
    }

    private static Class<?> compileAndLoadClass(String sourceCode, String className) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        
        JavaFileObject file = new JavaSourceFromString(className, sourceCode);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);
        
        boolean success = task.call();
        if (!success) {
            StringBuilder sb = new StringBuilder();
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                sb.append(diagnostic.toString()).append("\n");
            }
            throw new Exception("Compilation failed:\n" + sb.toString());
        }
        
        // Load and return the compiled class
        MemoryClassLoader classLoader = new MemoryClassLoader();
        return classLoader.loadClass(className);
    }

    private static String executeCompiledClass(Class<?> compiledClass) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream oldOut = System.out;
        System.setOut(ps);

        try {
            Method mainMethod = compiledClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[0]);
        } finally {
            System.out.flush();
            System.setOut(oldOut);
        }

        return baos.toString();
    }

    private static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    private static class MemoryClassLoader extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.startsWith("java.")) {
                return ClassLoader.getSystemClassLoader().loadClass(name);
            }
            
            byte[] classData = getClassData(name);
            if (classData == null) {
                throw new ClassNotFoundException();
            }
            return defineClass(name, classData, 0, classData.length);
        }

        private byte[] getClassData(String className) {
            String path = className.replace('.', '/') + ".class";
            try (InputStream inputStream = getResourceAsStream(path)) {
                if (inputStream == null) return null;
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                int nextValue = inputStream.read();
                while (-1 != nextValue) {
                    byteStream.write(nextValue);
                    nextValue = inputStream.read();
                }
                return byteStream.toByteArray();
            } catch (IOException e) {
                return null;
            }
        }
    }
}
