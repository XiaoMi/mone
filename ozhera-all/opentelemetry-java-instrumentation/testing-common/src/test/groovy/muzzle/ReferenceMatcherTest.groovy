/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package muzzle

import static io.opentelemetry.javaagent.extension.muzzle.Flag.ManifestationFlag.ABSTRACT
import static io.opentelemetry.javaagent.extension.muzzle.Flag.ManifestationFlag.INTERFACE
import static io.opentelemetry.javaagent.extension.muzzle.Flag.ManifestationFlag.NON_INTERFACE
import static io.opentelemetry.javaagent.extension.muzzle.Flag.MinimumVisibilityFlag.PACKAGE_OR_HIGHER
import static io.opentelemetry.javaagent.extension.muzzle.Flag.MinimumVisibilityFlag.PRIVATE_OR_HIGHER
import static io.opentelemetry.javaagent.extension.muzzle.Flag.MinimumVisibilityFlag.PROTECTED_OR_HIGHER
import static io.opentelemetry.javaagent.extension.muzzle.Flag.OwnershipFlag.NON_STATIC
import static io.opentelemetry.javaagent.extension.muzzle.Flag.OwnershipFlag.STATIC
import static io.opentelemetry.javaagent.tooling.muzzle.matcher.Mismatch.MissingClass
import static io.opentelemetry.javaagent.tooling.muzzle.matcher.Mismatch.MissingField
import static io.opentelemetry.javaagent.tooling.muzzle.matcher.Mismatch.MissingFlag
import static io.opentelemetry.javaagent.tooling.muzzle.matcher.Mismatch.MissingMethod
import static muzzle.TestClasses.MethodBodyAdvice

import external.LibraryBaseClass
import io.opentelemetry.instrumentation.TestHelperClasses
import io.opentelemetry.instrumentation.test.utils.ClasspathUtils
import io.opentelemetry.javaagent.extension.muzzle.ClassRef
import io.opentelemetry.javaagent.extension.muzzle.Flag
import io.opentelemetry.javaagent.extension.muzzle.Source
import io.opentelemetry.javaagent.tooling.muzzle.collector.ReferenceCollector
import io.opentelemetry.javaagent.tooling.muzzle.matcher.Mismatch
import io.opentelemetry.javaagent.tooling.muzzle.matcher.ReferenceMatcher
import net.bytebuddy.jar.asm.Type
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ReferenceMatcherTest extends Specification {
  static final TEST_EXTERNAL_INSTRUMENTATION_PACKAGE = "com.external.otel.instrumentation"

  @Shared
  ClassLoader safeClasspath = new URLClassLoader([ClasspathUtils.createJarWithClasses(MethodBodyAdvice.A,
    MethodBodyAdvice.B,
    MethodBodyAdvice.SomeInterface,
    MethodBodyAdvice.SomeImplementation)] as URL[],
    (ClassLoader) null)

  @Shared
  ClassLoader unsafeClasspath = new URLClassLoader([ClasspathUtils.createJarWithClasses(MethodBodyAdvice.A,
    MethodBodyAdvice.SomeInterface,
    MethodBodyAdvice.SomeImplementation)] as URL[],
    (ClassLoader) null)

  def "match safe classpaths"() {
    setup:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(MethodBodyAdvice.name)
    def refMatcher = createMatcher(collector.getReferences())

    expect:
    getMismatchClassSet(refMatcher.getMismatchedReferenceSources(safeClasspath)).empty
    getMismatchClassSet(refMatcher.getMismatchedReferenceSources(unsafeClasspath)) == [MissingClass] as Set
  }

  def "matching does not hold a strong reference to classloaders"() {
    expect:
    MuzzleWeakReferenceTest.classLoaderRefIsGarbageCollected()
  }

  private static class CountingClassLoader extends URLClassLoader {
    int count = 0

    CountingClassLoader(URL[] urls, ClassLoader parent) {
      super(urls, (ClassLoader) parent)
    }

    @Override
    URL getResource(String name) {
      count++
      return super.getResource(name)
    }
  }

  def "muzzle type pool caches"() {
    setup:
    def cl = new CountingClassLoader(
      [ClasspathUtils.createJarWithClasses(MethodBodyAdvice.A,
        MethodBodyAdvice.B,
        MethodBodyAdvice.SomeInterface,
        MethodBodyAdvice.SomeImplementation)] as URL[],
      (ClassLoader) null)

    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(MethodBodyAdvice.name)

    def refMatcher1 = createMatcher(collector.getReferences())
    def refMatcher2 = createMatcher(collector.getReferences())
    assert getMismatchClassSet(refMatcher1.getMismatchedReferenceSources(cl)).empty
    int countAfterFirstMatch = cl.count
    // the second matcher should be able to used cached type descriptions from the first
    assert getMismatchClassSet(refMatcher2.getMismatchedReferenceSources(cl)).empty

    expect:
    cl.count == countAfterFirstMatch
  }

  def "matching ref #referenceName #referenceFlags against #classToCheck produces #expectedMismatches"() {
    setup:
    def ref = ClassRef.newBuilder(referenceName)
      .addFlag(referenceFlag)
      .build()

    when:
    def mismatches = createMatcher([(ref.className): ref]).getMismatchedReferenceSources(this.class.classLoader)

    then:
    getMismatchClassSet(mismatches) == expectedMismatches as Set

    where:
    referenceName           | referenceFlag | classToCheck       | expectedMismatches
    MethodBodyAdvice.B.name | NON_INTERFACE | MethodBodyAdvice.B | []
    MethodBodyAdvice.B.name | INTERFACE     | MethodBodyAdvice.B | [MissingFlag]
  }

  def "method match #methodTestDesc"() {
    setup:
    def methodType = Type.getMethodType(methodDesc)
    def reference = ClassRef.newBuilder(classToCheck.name)
      .addMethod(new Source[0], methodFlags as Flag[], methodName, methodType.returnType, methodType.argumentTypes)
      .build()

    when:
    def mismatches = createMatcher([(reference.className): reference])
      .getMismatchedReferenceSources(this.class.classLoader)

    then:
    getMismatchClassSet(mismatches) == expectedMismatches as Set

    where:
    methodName      | methodDesc                               | methodFlags           | classToCheck                   | expectedMismatches | methodTestDesc
    "method"        | "(Ljava/lang/String;)Ljava/lang/String;" | []                    | MethodBodyAdvice.B             | []                 | "match method declared in class"
    "hashCode"      | "()I"                                    | []                    | MethodBodyAdvice.B             | []                 | "match method declared in superclass"
    "someMethod"    | "()V"                                    | []                    | MethodBodyAdvice.SomeInterface | []                 | "match method declared in interface"
    "privateStuff"  | "()V"                                    | [PRIVATE_OR_HIGHER]   | MethodBodyAdvice.B             | []                 | "match private method"
    "privateStuff"  | "()V"                                    | [PROTECTED_OR_HIGHER] | MethodBodyAdvice.B2            | [MissingFlag]      | "fail match private in supertype"
    "staticMethod"  | "()V"                                    | [NON_STATIC]          | MethodBodyAdvice.B             | [MissingFlag]      | "static method mismatch"
    "missingMethod" | "()V"                                    | []                    | MethodBodyAdvice.B             | [MissingMethod]    | "missing method mismatch"
  }

  def "field match #fieldTestDesc"() {
    setup:
    def reference = ClassRef.newBuilder(classToCheck.name)
      .addField(new Source[0], fieldFlags as Flag[], fieldName, Type.getType(fieldType), false)
      .build()

    when:
    def mismatches = createMatcher([(reference.className): reference])
      .getMismatchedReferenceSources(this.class.classLoader)

    then:
    getMismatchClassSet(mismatches) == expectedMismatches as Set

    where:
    fieldName        | fieldType                                        | fieldFlags                    | classToCheck                | expectedMismatches | fieldTestDesc
    "missingField"   | "Ljava/lang/String;"                             | []                            | MethodBodyAdvice.A          | [MissingField]     | "mismatch missing field"
    "privateField"   | "Ljava/lang/String;"                             | []                            | MethodBodyAdvice.A          | [MissingField]     | "mismatch field type signature"
    "privateField"   | "Ljava/lang/Object;"                             | [PRIVATE_OR_HIGHER]           | MethodBodyAdvice.A          | []                 | "match private field"
    "privateField"   | "Ljava/lang/Object;"                             | [PROTECTED_OR_HIGHER]         | MethodBodyAdvice.A2         | [MissingFlag]      | "mismatch private field in supertype"
    "protectedField" | "Ljava/lang/Object;"                             | [STATIC]                      | MethodBodyAdvice.A          | [MissingFlag]      | "mismatch static field"
    "staticB"        | Type.getType(MethodBodyAdvice.B).getDescriptor() | [STATIC, PROTECTED_OR_HIGHER] | MethodBodyAdvice.A          | []                 | "match static field"
    "number"         | "I"                                              | [PACKAGE_OR_HIGHER]           | MethodBodyAdvice.Primitives | []                 | "match primitive int"
    "flag"           | "Z"                                              | [PACKAGE_OR_HIGHER]           | MethodBodyAdvice.Primitives | []                 | "match primitive boolean"
  }

  def "should not check abstract #desc helper classes"() {
    given:
    def reference = ClassRef.newBuilder(className)
      .setSuperClassName(TestHelperClasses.HelperSuperClass.name)
      .addFlag(ABSTRACT)
      .addMethod(new Source[0], [ABSTRACT] as Flag[], "unimplemented", Type.VOID_TYPE)
      .build()

    when:
    def mismatches = createMatcher([(reference.className): reference], [reference.className])
      .getMismatchedReferenceSources(this.class.classLoader)

    then:
    mismatches.empty

    where:
    desc       | className
    "internal" | "io.opentelemetry.instrumentation.Helper"
    "external" | "${TEST_EXTERNAL_INSTRUMENTATION_PACKAGE}.Helper"
  }

  def "should not check #desc helper classes with no supertypes"() {
    given:
    def reference = ClassRef.newBuilder(className)
      .setSuperClassName(Object.name)
      .addMethod(new Source[0], [] as Flag[], "someMethod", Type.VOID_TYPE)
      .build()

    when:
    def mismatches = createMatcher([(reference.className): reference], [reference.className])
      .getMismatchedReferenceSources(this.class.classLoader)

    then:
    mismatches.empty

    where:
    desc       | className
    "internal" | "io.opentelemetry.instrumentation.Helper"
    "external" | "${TEST_EXTERNAL_INSTRUMENTATION_PACKAGE}.Helper"
  }

  def "should fail #desc helper classes that does not implement all abstract methods"() {
    given:
    def reference = ClassRef.newBuilder(className)
      .setSuperClassName(TestHelperClasses.HelperSuperClass.name)
      .addMethod(new Source[0], [] as Flag[], "someMethod", Type.VOID_TYPE)
      .build()

    when:
    def mismatches = createMatcher([(reference.className): reference], [reference.className])
      .getMismatchedReferenceSources(this.class.classLoader)

    then:
    getMismatchClassSet(mismatches) == [MissingMethod] as Set

    where:
    desc       | className
    "internal" | "io.opentelemetry.instrumentation.Helper"
    "external" | "${TEST_EXTERNAL_INSTRUMENTATION_PACKAGE}.Helper"
  }

  def "should fail #desc helper classes that do not implement all abstract methods - even if empty abstract class reference exists"() {
    given:
    def emptySuperClassRef = ClassRef.newBuilder(TestHelperClasses.HelperSuperClass.name)
      .build()
    def reference = ClassRef.newBuilder(className)
      .setSuperClassName(TestHelperClasses.HelperSuperClass.name)
      .addMethod(new Source[0], [] as Flag[], "someMethod", Type.VOID_TYPE)
      .build()

    when:
    def mismatches = createMatcher(
      [(reference.className): reference, (emptySuperClassRef.className): emptySuperClassRef],
      [reference.className, emptySuperClassRef.className])
      .getMismatchedReferenceSources(this.class.classLoader)

    then:
    getMismatchClassSet(mismatches) == [MissingMethod] as Set

    where:
    desc       | className
    "internal" | "io.opentelemetry.instrumentation.Helper"
    "external" | "${TEST_EXTERNAL_INSTRUMENTATION_PACKAGE}.Helper"
  }

  def "should check #desc helper class whether interface methods are implemented in the super class"() {
    given:
    def baseHelper = ClassRef.newBuilder("io.opentelemetry.instrumentation.BaseHelper")
      .setSuperClassName(Object.name)
      .addInterfaceName(TestHelperClasses.HelperInterface.name)
      .addMethod(new Source[0], [] as Flag[], "foo", Type.VOID_TYPE)
      .build()
    // abstract HelperInterface#foo() is implemented by BaseHelper
    def helper = ClassRef.newBuilder(className)
      .setSuperClassName(baseHelper.className)
      .addInterfaceName(TestHelperClasses.AnotherHelperInterface.name)
      .addMethod(new Source[0], [] as Flag[], "bar", Type.VOID_TYPE)
      .build()

    when:
    def mismatches = createMatcher(
      [(helper.className): helper, (baseHelper.className): baseHelper],
      [helper.className, baseHelper.className])
      .getMismatchedReferenceSources(this.class.classLoader)

    then:
    mismatches.empty

    where:
    desc       | className
    "internal" | "io.opentelemetry.instrumentation.Helper"
    "external" | "${TEST_EXTERNAL_INSTRUMENTATION_PACKAGE}.Helper"
  }

  def "should check #desc helper class whether used fields are declared in the super class"() {
    given:
    def helper = ClassRef.newBuilder(className)
      .setSuperClassName(LibraryBaseClass.name)
      .addField(new Source[0], new Flag[0], "field", Type.getType("Ljava/lang/Integer;"), false)
      .build()

    when:
    def mismatches = createMatcher([(helper.className): helper], [helper.className])
      .getMismatchedReferenceSources(this.class.classLoader)

    then:
    mismatches.empty

    where:
    desc       | className
    "internal" | "io.opentelemetry.instrumentation.Helper"
    "external" | "${TEST_EXTERNAL_INSTRUMENTATION_PACKAGE}.Helper"
  }

  def "should fail helper class when it uses fields undeclared in the super class: #desc"() {
    given:
    def helper = ClassRef.newBuilder(className)
      .setSuperClassName(LibraryBaseClass.name)
      .addField(new Source[0], new Flag[0], fieldName, Type.getType(fieldType), false)
      .build()

    when:
    def mismatches = createMatcher([(helper.className): helper], [helper.className])
      .getMismatchedReferenceSources(this.class.classLoader)

    then:
    getMismatchClassSet(mismatches) == [MissingField] as Set

    where:
    desc                                    | className                                         | fieldName        | fieldType
    "internal helper, different field name" | "io.opentelemetry.instrumentation.Helper"         | "differentField" | "Ljava/lang/Integer;"
    "internal helper, different field type" | "io.opentelemetry.instrumentation.Helper"         | "field"          | "Lcom/external/DifferentType;"
    "external helper, different field name" | "${TEST_EXTERNAL_INSTRUMENTATION_PACKAGE}.Helper" | "differentField" | "Ljava/lang/Integer;"
    "external helper, different field type" | "${TEST_EXTERNAL_INSTRUMENTATION_PACKAGE}.Helper" | "field"          | "Lcom/external/DifferentType;"
  }

  private static ReferenceMatcher createMatcher(Map<String, ClassRef> references = [:],
                                                List<String> helperClasses = []) {
    new ReferenceMatcher(helperClasses, references, { it.startsWith(TEST_EXTERNAL_INSTRUMENTATION_PACKAGE) })
  }

  private static Set<Class> getMismatchClassSet(List<Mismatch> mismatches) {
    Set<Class> mismatchClasses = new HashSet<>(mismatches.size())
    for (Mismatch mismatch : mismatches) {
      mismatchClasses.add(mismatch.class)
    }
    return mismatchClasses
  }
}
