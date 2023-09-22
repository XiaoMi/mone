/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package muzzle


import static io.opentelemetry.javaagent.extension.muzzle.Flag.ManifestationFlag
import static io.opentelemetry.javaagent.extension.muzzle.Flag.MinimumVisibilityFlag
import static io.opentelemetry.javaagent.extension.muzzle.Flag.OwnershipFlag
import static io.opentelemetry.javaagent.extension.muzzle.Flag.VisibilityFlag
import static muzzle.TestClasses.HelperAdvice
import static muzzle.TestClasses.LdcAdvice
import static muzzle.TestClasses.MethodBodyAdvice

import external.instrumentation.ExternalHelper
import io.opentelemetry.context.Context
import io.opentelemetry.instrumentation.InstrumentationContextTestClasses
import io.opentelemetry.instrumentation.OtherTestHelperClasses
import io.opentelemetry.instrumentation.TestHelperClasses
import io.opentelemetry.javaagent.extension.muzzle.ClassRef
import io.opentelemetry.javaagent.extension.muzzle.FieldRef
import io.opentelemetry.javaagent.extension.muzzle.Flag
import io.opentelemetry.javaagent.tooling.muzzle.collector.MuzzleCompilationException
import io.opentelemetry.javaagent.tooling.muzzle.collector.ReferenceCollector
import spock.lang.Specification
import spock.lang.Unroll

class ReferenceCollectorTest extends Specification {
  def "method body creates references"() {
    setup:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(MethodBodyAdvice.name)
    collector.prune()
    def references = collector.getReferences()

    expect:
    references.keySet() == [
      MethodBodyAdvice.A.name,
      MethodBodyAdvice.B.name,
      MethodBodyAdvice.SomeInterface.name,
      MethodBodyAdvice.SomeImplementation.name
    ] as Set

    def bRef = references[MethodBodyAdvice.B.name]
    def aRef = references[MethodBodyAdvice.A.name]

    // interface flags
    bRef.flags.contains(ManifestationFlag.NON_INTERFACE)
    references[MethodBodyAdvice.SomeInterface.name].flags.contains(ManifestationFlag.INTERFACE)

    // class access flags
    aRef.flags.contains(MinimumVisibilityFlag.PACKAGE_OR_HIGHER)
    bRef.flags.contains(MinimumVisibilityFlag.PACKAGE_OR_HIGHER)

    // method refs
    assertMethod bRef, 'method', '(Ljava/lang/String;)Ljava/lang/String;',
      MinimumVisibilityFlag.PROTECTED_OR_HIGHER,
      OwnershipFlag.NON_STATIC
    assertMethod bRef, 'methodWithPrimitives', '(Z)V',
      MinimumVisibilityFlag.PROTECTED_OR_HIGHER,
      OwnershipFlag.NON_STATIC
    assertMethod bRef, 'staticMethod', '()V',
      MinimumVisibilityFlag.PROTECTED_OR_HIGHER,
      OwnershipFlag.STATIC
    assertMethod bRef, 'methodWithArrays', '([Ljava/lang/String;)[Ljava/lang/Object;',
      MinimumVisibilityFlag.PROTECTED_OR_HIGHER,
      OwnershipFlag.NON_STATIC

    // field refs
    bRef.fields.isEmpty()
    aRef.fields.size() == 2
    assertField aRef, 'publicB', MinimumVisibilityFlag.PACKAGE_OR_HIGHER, OwnershipFlag.NON_STATIC
    assertField aRef, 'staticB', MinimumVisibilityFlag.PACKAGE_OR_HIGHER, OwnershipFlag.STATIC
  }

  def "protected ref test"() {
    setup:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(MethodBodyAdvice.B2.name)
    collector.prune()
    def references = collector.getReferences()

    expect:
    assertMethod references[MethodBodyAdvice.B.name], 'protectedMethod', '()V',
      MinimumVisibilityFlag.PROTECTED_OR_HIGHER,
      OwnershipFlag.NON_STATIC
  }

  def "ldc creates references"() {
    setup:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(LdcAdvice.name)
    collector.prune()
    def references = collector.getReferences()

    expect:
    references[MethodBodyAdvice.A.name] != null
  }

  def "instanceof creates references"() {
    setup:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(TestClasses.InstanceofAdvice.name)
    collector.prune()
    def references = collector.getReferences()

    expect:
    references[MethodBodyAdvice.A.name] != null
  }

  def "invokedynamic creates references"() {
    setup:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(TestClasses.InvokeDynamicAdvice.name)
    collector.prune()
    def references = collector.getReferences()

    expect:
    references['muzzle.TestClasses$MethodBodyAdvice$SomeImplementation'] != null
    references['muzzle.TestClasses$MethodBodyAdvice$B'] != null
  }

  def "should create references for helper classes"() {
    when:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(HelperAdvice.name)
    def references = collector.getReferences()

    then:
    references.keySet() == [
      TestHelperClasses.Helper.name,
      TestHelperClasses.HelperSuperClass.name,
      TestHelperClasses.HelperInterface.name
    ] as Set

    with(references[TestHelperClasses.HelperSuperClass.name]) { helperSuperClass ->
      helperSuperClass.flags.contains(ManifestationFlag.ABSTRACT)
      assertHelperSuperClassMethod(helperSuperClass, true)
      assertMethod helperSuperClass, 'finalMethod', '()Ljava/lang/String;',
        VisibilityFlag.PUBLIC,
        OwnershipFlag.NON_STATIC,
        ManifestationFlag.FINAL
    }

    with(references[TestHelperClasses.HelperInterface.name]) { helperInterface ->
      helperInterface.flags.contains(ManifestationFlag.ABSTRACT)
      assertHelperInterfaceMethod helperInterface, true
    }

    with(references[TestHelperClasses.Helper.name]) { helperClass ->
      helperClass.flags.contains(ManifestationFlag.NON_FINAL)
      assertHelperSuperClassMethod helperClass, false
      assertHelperInterfaceMethod helperClass, false
    }
  }

  def "should collect field declaration references"() {
    when:
    def collector = new ReferenceCollector({ it == DeclaredFieldTestClass.Helper.name })
    collector.collectReferencesFromAdvice(DeclaredFieldTestClass.Advice.name)
    collector.prune()
    def references = collector.references

    then:
    println references

    with(references[DeclaredFieldTestClass.Helper.name]) { helperClass ->
      def superField = findField(helperClass, 'superField')
      !superField.declared

      def field = findField(helperClass, 'helperField')
      field.declared
    }

    with(references[DeclaredFieldTestClass.LibraryBaseClass.name]) { libraryBaseClass ->
      libraryBaseClass.fields.empty
    }
  }

  def "should find all helper classes"() {
    when:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(HelperAdvice.name)
    collector.prune()
    def helperClasses = collector.getSortedHelperClasses()

    then:
    assertThatContainsInOrder helperClasses, [
      TestHelperClasses.HelperInterface.name,
      TestHelperClasses.Helper.name
    ]
    assertThatContainsInOrder helperClasses, [
      TestHelperClasses.HelperSuperClass.name,
      TestHelperClasses.Helper.name
    ]
  }

  def "should correctly find helper classes from multiple advice classes"() {
    when:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(TestClasses.HelperAdvice.name)
    collector.collectReferencesFromAdvice(TestClasses.HelperOtherAdvice.name)
    collector.prune()
    def helperClasses = collector.getSortedHelperClasses()

    then:
    assertThatContainsInOrder helperClasses, [
      TestHelperClasses.HelperInterface.name,
      TestHelperClasses.Helper.name
    ]
    assertThatContainsInOrder helperClasses, [
      TestHelperClasses.HelperSuperClass.name,
      TestHelperClasses.Helper.name
    ]
    assertThatContainsInOrder helperClasses, [
      OtherTestHelperClasses.TestEnum.name,
      OtherTestHelperClasses.TestEnum.name + '$1',
    ]
    new HashSet<>(helperClasses) == new HashSet([
      TestHelperClasses.HelperSuperClass.name,
      TestHelperClasses.HelperInterface.name,
      TestHelperClasses.Helper.name,
      OtherTestHelperClasses.Bar.name,
      OtherTestHelperClasses.Foo.name,
      OtherTestHelperClasses.TestEnum.name,
      OtherTestHelperClasses.TestEnum.name + '$1',
      OtherTestHelperClasses.name + '$1',
    ])
  }

  def "should correctly find external instrumentation classes"() {
    when:
    def collector = new ReferenceCollector({ it.startsWith("external.instrumentation") })
    collector.collectReferencesFromAdvice(TestClasses.ExternalInstrumentationAdvice.name)
    collector.prune()

    then: "should collect references"
    def references = collector.getReferences()
    references['external.NotInstrumentation'] != null

    then: "should collect helper classes"
    def helperClasses = collector.getSortedHelperClasses()
    helperClasses == [ExternalHelper.name]
  }

  @Unroll
  def "should collect helper classes from resource file #desc"() {
    when:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromResource(resource)
    collector.prune()

    then: "SPI classes are included in helper classes"
    def helperClasses = collector.sortedHelperClasses
    assertThatContainsInOrder helperClasses, [
      TestHelperClasses.HelperInterface.name,
      TestHelperClasses.Helper.name
    ]
    assertThatContainsInOrder helperClasses, [
      TestHelperClasses.HelperSuperClass.name,
      TestHelperClasses.Helper.name
    ]

    where:
    desc                                                  | resource
    "Java SPI"                                            | "META-INF/services/test.resource.file"
    "AWS SDK v2 global interceptors file"                 | "software/amazon/awssdk/global/handlers/execution.interceptors"
    "AWS SDK v2 service interceptors file"                | "software/amazon/awssdk/services/testservice/execution.interceptors"
    "AWS SDK v2 service (second level) interceptors file" | "software/amazon/awssdk/services/testservice/testsubservice/execution.interceptors"
    "AWS SDK v1 global interceptors file"                 | "com/amazonaws/global/handlers/request.handler2s"
    "AWS SDK v1 service interceptors file"                | "com/amazonaws/services/testservice/request.handler2s"
    "AWS SDK v1 service (second level) interceptors file" | "com/amazonaws/services/testservice/testsubservice/request.handler2s"
  }

  def "should ignore arbitrary resource file"() {
    when:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromResource("application.properties")
    collector.prune()

    then:
    collector.references.isEmpty()
    collector.sortedHelperClasses.isEmpty()
  }

  def "should collect context store classes"() {
    when:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(InstrumentationContextTestClasses.ValidAdvice.name)
    collector.prune()

    then:
    def contextStore = collector.getContextStoreClasses()
    contextStore == [
      (InstrumentationContextTestClasses.Key1.name): Context.name,
      (InstrumentationContextTestClasses.Key2.name): Context.name
    ]
  }

  def "should not collect context store classes for invalid scenario: #desc"() {
    when:
    def collector = new ReferenceCollector({ false })
    collector.collectReferencesFromAdvice(adviceClassName)
    collector.prune()

    then:
    thrown(MuzzleCompilationException)

    where:
    desc                                                                        | adviceClassName
    "passing arbitrary variables or parameters to InstrumentationContext.get()" | InstrumentationContextTestClasses.NotUsingClassRefAdvice.name
    "storing class ref in a local var"                                          | InstrumentationContextTestClasses.PassingVariableAdvice.name
  }

  private static assertHelperSuperClassMethod(ClassRef reference, boolean isAbstract) {
    assertMethod reference, 'abstractMethod', '()I',
      VisibilityFlag.PROTECTED,
      OwnershipFlag.NON_STATIC,
      isAbstract ? ManifestationFlag.ABSTRACT : ManifestationFlag.NON_FINAL
  }

  private static assertHelperInterfaceMethod(ClassRef reference, boolean isAbstract) {
    assertMethod reference, 'foo', '()V',
      VisibilityFlag.PUBLIC,
      OwnershipFlag.NON_STATIC,
      isAbstract ? ManifestationFlag.ABSTRACT : ManifestationFlag.NON_FINAL
  }

  private static assertMethod(ClassRef reference, String methodName, String methodDesc, Flag... flags) {
    def method = findMethod reference, methodName, methodDesc
    method != null && (method.flags == flags as Set)
  }

  private static findMethod(ClassRef reference, String methodName, String methodDesc) {
    for (def method : reference.methods) {
      if (method.name == methodName && method.descriptor == methodDesc) {
        return method
      }
    }
    return null
  }

  private static assertField(ClassRef reference, String fieldName, Flag... flags) {
    def field = findField reference, fieldName
    field != null && (field.flags == flags as Set)
  }

  private static FieldRef findField(ClassRef reference, String fieldName) {
    for (def field : reference.fields) {
      if (field.name == fieldName) {
        return field
      }
    }
    return null
  }

  private static assertThatContainsInOrder(List<String> list, List<String> sublist) {
    def listIt = list.iterator()
    def sublistIt = sublist.iterator()
    while (listIt.hasNext() && sublistIt.hasNext()) {
      def sublistElem = sublistIt.next()
      while (listIt.hasNext()) {
        def listElem = listIt.next()
        if (listElem == sublistElem) {
          break
        }
      }
    }
    return !sublistIt.hasNext()
  }
}
