/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

// Generated from Java8.g4 by ANTLR 4.7.1
package com.xiaomi.data.push.antlr.java8;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Java8Parser}.
 */
public interface Java8Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Java8Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(Java8Parser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(Java8Parser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(Java8Parser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(Java8Parser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(Java8Parser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(Java8Parser.PrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#numericType}.
	 * @param ctx the parse tree
	 */
	void enterNumericType(Java8Parser.NumericTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#numericType}.
	 * @param ctx the parse tree
	 */
	void exitNumericType(Java8Parser.NumericTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#integralType}.
	 * @param ctx the parse tree
	 */
	void enterIntegralType(Java8Parser.IntegralTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#integralType}.
	 * @param ctx the parse tree
	 */
	void exitIntegralType(Java8Parser.IntegralTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#floatingPointType}.
	 * @param ctx the parse tree
	 */
	void enterFloatingPointType(Java8Parser.FloatingPointTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#floatingPointType}.
	 * @param ctx the parse tree
	 */
	void exitFloatingPointType(Java8Parser.FloatingPointTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#referenceType}.
	 * @param ctx the parse tree
	 */
	void enterReferenceType(Java8Parser.ReferenceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#referenceType}.
	 * @param ctx the parse tree
	 */
	void exitReferenceType(Java8Parser.ReferenceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterClassOrInterfaceType(Java8Parser.ClassOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitClassOrInterfaceType(Java8Parser.ClassOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classType}.
	 * @param ctx the parse tree
	 */
	void enterClassType(Java8Parser.ClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classType}.
	 * @param ctx the parse tree
	 */
	void exitClassType(Java8Parser.ClassTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classType_lf_classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterClassType_lf_classOrInterfaceType(Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classType_lf_classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitClassType_lf_classOrInterfaceType(Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classType_lfno_classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterClassType_lfno_classOrInterfaceType(Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classType_lfno_classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitClassType_lfno_classOrInterfaceType(Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceType}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceType(Java8Parser.InterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceType}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceType(Java8Parser.InterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceType_lf_classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceType_lf_classOrInterfaceType(Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceType_lf_classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceType_lf_classOrInterfaceType(Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceType_lfno_classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceType_lfno_classOrInterfaceType(Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceType_lfno_classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceType_lfno_classOrInterfaceType(Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeVariable}.
	 * @param ctx the parse tree
	 */
	void enterTypeVariable(Java8Parser.TypeVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeVariable}.
	 * @param ctx the parse tree
	 */
	void exitTypeVariable(Java8Parser.TypeVariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#arrayType}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(Java8Parser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#arrayType}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(Java8Parser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#dims}.
	 * @param ctx the parse tree
	 */
	void enterDims(Java8Parser.DimsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#dims}.
	 * @param ctx the parse tree
	 */
	void exitDims(Java8Parser.DimsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameter(Java8Parser.TypeParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameter(Java8Parser.TypeParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeParameterModifier}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameterModifier(Java8Parser.TypeParameterModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeParameterModifier}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameterModifier(Java8Parser.TypeParameterModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeBound}.
	 * @param ctx the parse tree
	 */
	void enterTypeBound(Java8Parser.TypeBoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeBound}.
	 * @param ctx the parse tree
	 */
	void exitTypeBound(Java8Parser.TypeBoundContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#additionalBound}.
	 * @param ctx the parse tree
	 */
	void enterAdditionalBound(Java8Parser.AdditionalBoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#additionalBound}.
	 * @param ctx the parse tree
	 */
	void exitAdditionalBound(Java8Parser.AdditionalBoundContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void enterTypeArguments(Java8Parser.TypeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void exitTypeArguments(Java8Parser.TypeArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeArgumentList}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgumentList(Java8Parser.TypeArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeArgumentList}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgumentList(Java8Parser.TypeArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgument(Java8Parser.TypeArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgument(Java8Parser.TypeArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#wildcard}.
	 * @param ctx the parse tree
	 */
	void enterWildcard(Java8Parser.WildcardContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#wildcard}.
	 * @param ctx the parse tree
	 */
	void exitWildcard(Java8Parser.WildcardContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#wildcardBounds}.
	 * @param ctx the parse tree
	 */
	void enterWildcardBounds(Java8Parser.WildcardBoundsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#wildcardBounds}.
	 * @param ctx the parse tree
	 */
	void exitWildcardBounds(Java8Parser.WildcardBoundsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#packageName}.
	 * @param ctx the parse tree
	 */
	void enterPackageName(Java8Parser.PackageNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#packageName}.
	 * @param ctx the parse tree
	 */
	void exitPackageName(Java8Parser.PackageNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterTypeName(Java8Parser.TypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitTypeName(Java8Parser.TypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#packageOrTypeName}.
	 * @param ctx the parse tree
	 */
	void enterPackageOrTypeName(Java8Parser.PackageOrTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#packageOrTypeName}.
	 * @param ctx the parse tree
	 */
	void exitPackageOrTypeName(Java8Parser.PackageOrTypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#expressionName}.
	 * @param ctx the parse tree
	 */
	void enterExpressionName(Java8Parser.ExpressionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#expressionName}.
	 * @param ctx the parse tree
	 */
	void exitExpressionName(Java8Parser.ExpressionNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodName}.
	 * @param ctx the parse tree
	 */
	void enterMethodName(Java8Parser.MethodNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodName}.
	 * @param ctx the parse tree
	 */
	void exitMethodName(Java8Parser.MethodNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#ambiguousName}.
	 * @param ctx the parse tree
	 */
	void enterAmbiguousName(Java8Parser.AmbiguousNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#ambiguousName}.
	 * @param ctx the parse tree
	 */
	void exitAmbiguousName(Java8Parser.AmbiguousNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(Java8Parser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(Java8Parser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPackageDeclaration(Java8Parser.PackageDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPackageDeclaration(Java8Parser.PackageDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#packageModifier}.
	 * @param ctx the parse tree
	 */
	void enterPackageModifier(Java8Parser.PackageModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#packageModifier}.
	 * @param ctx the parse tree
	 */
	void exitPackageModifier(Java8Parser.PackageModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration(Java8Parser.ImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration(Java8Parser.ImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#singleTypeImportDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterSingleTypeImportDeclaration(Java8Parser.SingleTypeImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#singleTypeImportDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitSingleTypeImportDeclaration(Java8Parser.SingleTypeImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeImportOnDemandDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeImportOnDemandDeclaration(Java8Parser.TypeImportOnDemandDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeImportOnDemandDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeImportOnDemandDeclaration(Java8Parser.TypeImportOnDemandDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#singleStaticImportDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterSingleStaticImportDeclaration(Java8Parser.SingleStaticImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#singleStaticImportDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitSingleStaticImportDeclaration(Java8Parser.SingleStaticImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#staticImportOnDemandDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStaticImportOnDemandDeclaration(Java8Parser.StaticImportOnDemandDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#staticImportOnDemandDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStaticImportOnDemandDeclaration(Java8Parser.StaticImportOnDemandDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration(Java8Parser.TypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration(Java8Parser.TypeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(Java8Parser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(Java8Parser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#normalClassDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#normalClassDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classModifier}.
	 * @param ctx the parse tree
	 */
	void enterClassModifier(Java8Parser.ClassModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classModifier}.
	 * @param ctx the parse tree
	 */
	void exitClassModifier(Java8Parser.ClassModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameters(Java8Parser.TypeParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameters(Java8Parser.TypeParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeParameterList}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameterList(Java8Parser.TypeParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeParameterList}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameterList(Java8Parser.TypeParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#superclass}.
	 * @param ctx the parse tree
	 */
	void enterSuperclass(Java8Parser.SuperclassContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#superclass}.
	 * @param ctx the parse tree
	 */
	void exitSuperclass(Java8Parser.SuperclassContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#superinterfaces}.
	 * @param ctx the parse tree
	 */
	void enterSuperinterfaces(Java8Parser.SuperinterfacesContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#superinterfaces}.
	 * @param ctx the parse tree
	 */
	void exitSuperinterfaces(Java8Parser.SuperinterfacesContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceTypeList}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceTypeList(Java8Parser.InterfaceTypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceTypeList}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceTypeList(Java8Parser.InterfaceTypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(Java8Parser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(Java8Parser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassBodyDeclaration(Java8Parser.ClassBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassBodyDeclaration(Java8Parser.ClassBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassMemberDeclaration(Java8Parser.ClassMemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassMemberDeclaration(Java8Parser.ClassMemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclaration(Java8Parser.FieldDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclaration(Java8Parser.FieldDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#fieldModifier}.
	 * @param ctx the parse tree
	 */
	void enterFieldModifier(Java8Parser.FieldModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#fieldModifier}.
	 * @param ctx the parse tree
	 */
	void exitFieldModifier(Java8Parser.FieldModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#variableDeclaratorList}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorList(Java8Parser.VariableDeclaratorListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#variableDeclaratorList}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorList(Java8Parser.VariableDeclaratorListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator(Java8Parser.VariableDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator(Java8Parser.VariableDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorId(Java8Parser.VariableDeclaratorIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorId(Java8Parser.VariableDeclaratorIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitializer(Java8Parser.VariableInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitializer(Java8Parser.VariableInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannType}.
	 * @param ctx the parse tree
	 */
	void enterUnannType(Java8Parser.UnannTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannType}.
	 * @param ctx the parse tree
	 */
	void exitUnannType(Java8Parser.UnannTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannPrimitiveType}.
	 * @param ctx the parse tree
	 */
	void enterUnannPrimitiveType(Java8Parser.UnannPrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannPrimitiveType}.
	 * @param ctx the parse tree
	 */
	void exitUnannPrimitiveType(Java8Parser.UnannPrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannReferenceType}.
	 * @param ctx the parse tree
	 */
	void enterUnannReferenceType(Java8Parser.UnannReferenceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannReferenceType}.
	 * @param ctx the parse tree
	 */
	void exitUnannReferenceType(Java8Parser.UnannReferenceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterUnannClassOrInterfaceType(Java8Parser.UnannClassOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitUnannClassOrInterfaceType(Java8Parser.UnannClassOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannClassType}.
	 * @param ctx the parse tree
	 */
	void enterUnannClassType(Java8Parser.UnannClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannClassType}.
	 * @param ctx the parse tree
	 */
	void exitUnannClassType(Java8Parser.UnannClassTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannClassType_lf_unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterUnannClassType_lf_unannClassOrInterfaceType(Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannClassType_lf_unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitUnannClassType_lf_unannClassOrInterfaceType(Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannClassType_lfno_unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterUnannClassType_lfno_unannClassOrInterfaceType(Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannClassType_lfno_unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitUnannClassType_lfno_unannClassOrInterfaceType(Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterUnannInterfaceType(Java8Parser.UnannInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitUnannInterfaceType(Java8Parser.UnannInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannInterfaceType_lf_unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterUnannInterfaceType_lf_unannClassOrInterfaceType(Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannInterfaceType_lf_unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitUnannInterfaceType_lf_unannClassOrInterfaceType(Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannInterfaceType_lfno_unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterUnannInterfaceType_lfno_unannClassOrInterfaceType(Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannInterfaceType_lfno_unannClassOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitUnannInterfaceType_lfno_unannClassOrInterfaceType(Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannTypeVariable}.
	 * @param ctx the parse tree
	 */
	void enterUnannTypeVariable(Java8Parser.UnannTypeVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannTypeVariable}.
	 * @param ctx the parse tree
	 */
	void exitUnannTypeVariable(Java8Parser.UnannTypeVariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unannArrayType}.
	 * @param ctx the parse tree
	 */
	void enterUnannArrayType(Java8Parser.UnannArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unannArrayType}.
	 * @param ctx the parse tree
	 */
	void exitUnannArrayType(Java8Parser.UnannArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodModifier}.
	 * @param ctx the parse tree
	 */
	void enterMethodModifier(Java8Parser.MethodModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodModifier}.
	 * @param ctx the parse tree
	 */
	void exitMethodModifier(Java8Parser.MethodModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodHeader}.
	 * @param ctx the parse tree
	 */
	void enterMethodHeader(Java8Parser.MethodHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodHeader}.
	 * @param ctx the parse tree
	 */
	void exitMethodHeader(Java8Parser.MethodHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#result}.
	 * @param ctx the parse tree
	 */
	void enterResult(Java8Parser.ResultContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#result}.
	 * @param ctx the parse tree
	 */
	void exitResult(Java8Parser.ResultContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterList(Java8Parser.FormalParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterList(Java8Parser.FormalParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameters(Java8Parser.FormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameters(Java8Parser.FormalParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter(Java8Parser.FormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter(Java8Parser.FormalParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#variableModifier}.
	 * @param ctx the parse tree
	 */
	void enterVariableModifier(Java8Parser.VariableModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#variableModifier}.
	 * @param ctx the parse tree
	 */
	void exitVariableModifier(Java8Parser.VariableModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#lastFormalParameter}.
	 * @param ctx the parse tree
	 */
	void enterLastFormalParameter(Java8Parser.LastFormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#lastFormalParameter}.
	 * @param ctx the parse tree
	 */
	void exitLastFormalParameter(Java8Parser.LastFormalParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#receiverParameter}.
	 * @param ctx the parse tree
	 */
	void enterReceiverParameter(Java8Parser.ReceiverParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#receiverParameter}.
	 * @param ctx the parse tree
	 */
	void exitReceiverParameter(Java8Parser.ReceiverParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#throws_}.
	 * @param ctx the parse tree
	 */
	void enterThrows_(Java8Parser.Throws_Context ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#throws_}.
	 * @param ctx the parse tree
	 */
	void exitThrows_(Java8Parser.Throws_Context ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#exceptionTypeList}.
	 * @param ctx the parse tree
	 */
	void enterExceptionTypeList(Java8Parser.ExceptionTypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#exceptionTypeList}.
	 * @param ctx the parse tree
	 */
	void exitExceptionTypeList(Java8Parser.ExceptionTypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#exceptionType}.
	 * @param ctx the parse tree
	 */
	void enterExceptionType(Java8Parser.ExceptionTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#exceptionType}.
	 * @param ctx the parse tree
	 */
	void exitExceptionType(Java8Parser.ExceptionTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void enterMethodBody(Java8Parser.MethodBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void exitMethodBody(Java8Parser.MethodBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#instanceInitializer}.
	 * @param ctx the parse tree
	 */
	void enterInstanceInitializer(Java8Parser.InstanceInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#instanceInitializer}.
	 * @param ctx the parse tree
	 */
	void exitInstanceInitializer(Java8Parser.InstanceInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#staticInitializer}.
	 * @param ctx the parse tree
	 */
	void enterStaticInitializer(Java8Parser.StaticInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#staticInitializer}.
	 * @param ctx the parse tree
	 */
	void exitStaticInitializer(Java8Parser.StaticInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#constructorModifier}.
	 * @param ctx the parse tree
	 */
	void enterConstructorModifier(Java8Parser.ConstructorModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constructorModifier}.
	 * @param ctx the parse tree
	 */
	void exitConstructorModifier(Java8Parser.ConstructorModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#constructorDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclarator(Java8Parser.ConstructorDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constructorDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclarator(Java8Parser.ConstructorDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#simpleTypeName}.
	 * @param ctx the parse tree
	 */
	void enterSimpleTypeName(Java8Parser.SimpleTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#simpleTypeName}.
	 * @param ctx the parse tree
	 */
	void exitSimpleTypeName(Java8Parser.SimpleTypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void enterConstructorBody(Java8Parser.ConstructorBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void exitConstructorBody(Java8Parser.ConstructorBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void enterExplicitConstructorInvocation(Java8Parser.ExplicitConstructorInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void exitExplicitConstructorInvocation(Java8Parser.ExplicitConstructorInvocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterEnumDeclaration(Java8Parser.EnumDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitEnumDeclaration(Java8Parser.EnumDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumBody}.
	 * @param ctx the parse tree
	 */
	void enterEnumBody(Java8Parser.EnumBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumBody}.
	 * @param ctx the parse tree
	 */
	void exitEnumBody(Java8Parser.EnumBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumConstantList}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstantList(Java8Parser.EnumConstantListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumConstantList}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstantList(Java8Parser.EnumConstantListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumConstant}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstant(Java8Parser.EnumConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumConstant}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstant(Java8Parser.EnumConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumConstantModifier}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstantModifier(Java8Parser.EnumConstantModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumConstantModifier}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstantModifier(Java8Parser.EnumConstantModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumBodyDeclarations}.
	 * @param ctx the parse tree
	 */
	void enterEnumBodyDeclarations(Java8Parser.EnumBodyDeclarationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumBodyDeclarations}.
	 * @param ctx the parse tree
	 */
	void exitEnumBodyDeclarations(Java8Parser.EnumBodyDeclarationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceDeclaration(Java8Parser.InterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceDeclaration(Java8Parser.InterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#normalInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#normalInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceModifier}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceModifier(Java8Parser.InterfaceModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceModifier}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceModifier(Java8Parser.InterfaceModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#extendsInterfaces}.
	 * @param ctx the parse tree
	 */
	void enterExtendsInterfaces(Java8Parser.ExtendsInterfacesContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#extendsInterfaces}.
	 * @param ctx the parse tree
	 */
	void exitExtendsInterfaces(Java8Parser.ExtendsInterfacesContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceBody(Java8Parser.InterfaceBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceBody(Java8Parser.InterfaceBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMemberDeclaration(Java8Parser.InterfaceMemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMemberDeclaration(Java8Parser.InterfaceMemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#constantDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstantDeclaration(Java8Parser.ConstantDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constantDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstantDeclaration(Java8Parser.ConstantDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#constantModifier}.
	 * @param ctx the parse tree
	 */
	void enterConstantModifier(Java8Parser.ConstantModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constantModifier}.
	 * @param ctx the parse tree
	 */
	void exitConstantModifier(Java8Parser.ConstantModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMethodDeclaration(Java8Parser.InterfaceMethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMethodDeclaration(Java8Parser.InterfaceMethodDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#interfaceMethodModifier}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMethodModifier(Java8Parser.InterfaceMethodModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#interfaceMethodModifier}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMethodModifier(Java8Parser.InterfaceMethodModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeDeclaration(Java8Parser.AnnotationTypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeDeclaration(Java8Parser.AnnotationTypeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationTypeBody}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeBody(Java8Parser.AnnotationTypeBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationTypeBody}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeBody(Java8Parser.AnnotationTypeBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationTypeMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeMemberDeclaration(Java8Parser.AnnotationTypeMemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationTypeMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeMemberDeclaration(Java8Parser.AnnotationTypeMemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationTypeElementDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeElementDeclaration(Java8Parser.AnnotationTypeElementDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationTypeElementDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeElementDeclaration(Java8Parser.AnnotationTypeElementDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotationTypeElementModifier}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeElementModifier(Java8Parser.AnnotationTypeElementModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotationTypeElementModifier}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeElementModifier(Java8Parser.AnnotationTypeElementModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void enterDefaultValue(Java8Parser.DefaultValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void exitDefaultValue(Java8Parser.DefaultValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#annotation}.
	 * @param ctx the parse tree
	 */
	void enterAnnotation(Java8Parser.AnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#annotation}.
	 * @param ctx the parse tree
	 */
	void exitAnnotation(Java8Parser.AnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#normalAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterNormalAnnotation(Java8Parser.NormalAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#normalAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitNormalAnnotation(Java8Parser.NormalAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#elementValuePairList}.
	 * @param ctx the parse tree
	 */
	void enterElementValuePairList(Java8Parser.ElementValuePairListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#elementValuePairList}.
	 * @param ctx the parse tree
	 */
	void exitElementValuePairList(Java8Parser.ElementValuePairListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#elementValuePair}.
	 * @param ctx the parse tree
	 */
	void enterElementValuePair(Java8Parser.ElementValuePairContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#elementValuePair}.
	 * @param ctx the parse tree
	 */
	void exitElementValuePair(Java8Parser.ElementValuePairContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#elementValue}.
	 * @param ctx the parse tree
	 */
	void enterElementValue(Java8Parser.ElementValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#elementValue}.
	 * @param ctx the parse tree
	 */
	void exitElementValue(Java8Parser.ElementValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#elementValueArrayInitializer}.
	 * @param ctx the parse tree
	 */
	void enterElementValueArrayInitializer(Java8Parser.ElementValueArrayInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#elementValueArrayInitializer}.
	 * @param ctx the parse tree
	 */
	void exitElementValueArrayInitializer(Java8Parser.ElementValueArrayInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#elementValueList}.
	 * @param ctx the parse tree
	 */
	void enterElementValueList(Java8Parser.ElementValueListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#elementValueList}.
	 * @param ctx the parse tree
	 */
	void exitElementValueList(Java8Parser.ElementValueListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#markerAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterMarkerAnnotation(Java8Parser.MarkerAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#markerAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitMarkerAnnotation(Java8Parser.MarkerAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#singleElementAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterSingleElementAnnotation(Java8Parser.SingleElementAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#singleElementAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitSingleElementAnnotation(Java8Parser.SingleElementAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void enterArrayInitializer(Java8Parser.ArrayInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void exitArrayInitializer(Java8Parser.ArrayInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#variableInitializerList}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitializerList(Java8Parser.VariableInitializerListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#variableInitializerList}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitializerList(Java8Parser.VariableInitializerListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(Java8Parser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(Java8Parser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#blockStatements}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatements(Java8Parser.BlockStatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#blockStatements}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatements(Java8Parser.BlockStatementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(Java8Parser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(Java8Parser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclarationStatement(Java8Parser.LocalVariableDeclarationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclarationStatement(Java8Parser.LocalVariableDeclarationStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclaration(Java8Parser.LocalVariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclaration(Java8Parser.LocalVariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(Java8Parser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(Java8Parser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#statementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterStatementNoShortIf(Java8Parser.StatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#statementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitStatementNoShortIf(Java8Parser.StatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#statementWithoutTrailingSubstatement}.
	 * @param ctx the parse tree
	 */
	void enterStatementWithoutTrailingSubstatement(Java8Parser.StatementWithoutTrailingSubstatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#statementWithoutTrailingSubstatement}.
	 * @param ctx the parse tree
	 */
	void exitStatementWithoutTrailingSubstatement(Java8Parser.StatementWithoutTrailingSubstatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#emptyStatement}.
	 * @param ctx the parse tree
	 */
	void enterEmptyStatement(Java8Parser.EmptyStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#emptyStatement}.
	 * @param ctx the parse tree
	 */
	void exitEmptyStatement(Java8Parser.EmptyStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#labeledStatement}.
	 * @param ctx the parse tree
	 */
	void enterLabeledStatement(Java8Parser.LabeledStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#labeledStatement}.
	 * @param ctx the parse tree
	 */
	void exitLabeledStatement(Java8Parser.LabeledStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#labeledStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterLabeledStatementNoShortIf(Java8Parser.LabeledStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#labeledStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitLabeledStatementNoShortIf(Java8Parser.LabeledStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(Java8Parser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(Java8Parser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void enterStatementExpression(Java8Parser.StatementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void exitStatementExpression(Java8Parser.StatementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#ifThenStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfThenStatement(Java8Parser.IfThenStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#ifThenStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfThenStatement(Java8Parser.IfThenStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#ifThenElseStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#ifThenElseStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#ifThenElseStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterIfThenElseStatementNoShortIf(Java8Parser.IfThenElseStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#ifThenElseStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitIfThenElseStatementNoShortIf(Java8Parser.IfThenElseStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#assertStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssertStatement(Java8Parser.AssertStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#assertStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssertStatement(Java8Parser.AssertStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void enterSwitchStatement(Java8Parser.SwitchStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void exitSwitchStatement(Java8Parser.SwitchStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#switchBlock}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlock(Java8Parser.SwitchBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#switchBlock}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlock(Java8Parser.SwitchBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlockStatementGroup(Java8Parser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlockStatementGroup(Java8Parser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#switchLabels}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabels(Java8Parser.SwitchLabelsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#switchLabels}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabels(Java8Parser.SwitchLabelsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabel(Java8Parser.SwitchLabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabel(Java8Parser.SwitchLabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#enumConstantName}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstantName(Java8Parser.EnumConstantNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enumConstantName}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstantName(Java8Parser.EnumConstantNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(Java8Parser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(Java8Parser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#whileStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatementNoShortIf(Java8Parser.WhileStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#whileStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatementNoShortIf(Java8Parser.WhileStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#doStatement}.
	 * @param ctx the parse tree
	 */
	void enterDoStatement(Java8Parser.DoStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#doStatement}.
	 * @param ctx the parse tree
	 */
	void exitDoStatement(Java8Parser.DoStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(Java8Parser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(Java8Parser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#forStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterForStatementNoShortIf(Java8Parser.ForStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#forStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitForStatementNoShortIf(Java8Parser.ForStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#basicForStatement}.
	 * @param ctx the parse tree
	 */
	void enterBasicForStatement(Java8Parser.BasicForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#basicForStatement}.
	 * @param ctx the parse tree
	 */
	void exitBasicForStatement(Java8Parser.BasicForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#basicForStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterBasicForStatementNoShortIf(Java8Parser.BasicForStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#basicForStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitBasicForStatementNoShortIf(Java8Parser.BasicForStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInit(Java8Parser.ForInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInit(Java8Parser.ForInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void enterForUpdate(Java8Parser.ForUpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void exitForUpdate(Java8Parser.ForUpdateContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#statementExpressionList}.
	 * @param ctx the parse tree
	 */
	void enterStatementExpressionList(Java8Parser.StatementExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#statementExpressionList}.
	 * @param ctx the parse tree
	 */
	void exitStatementExpressionList(Java8Parser.StatementExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#enhancedForStatement}.
	 * @param ctx the parse tree
	 */
	void enterEnhancedForStatement(Java8Parser.EnhancedForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enhancedForStatement}.
	 * @param ctx the parse tree
	 */
	void exitEnhancedForStatement(Java8Parser.EnhancedForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#enhancedForStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void enterEnhancedForStatementNoShortIf(Java8Parser.EnhancedForStatementNoShortIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#enhancedForStatementNoShortIf}.
	 * @param ctx the parse tree
	 */
	void exitEnhancedForStatementNoShortIf(Java8Parser.EnhancedForStatementNoShortIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStatement(Java8Parser.BreakStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStatement(Java8Parser.BreakStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStatement(Java8Parser.ContinueStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStatement(Java8Parser.ContinueStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(Java8Parser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(Java8Parser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#throwStatement}.
	 * @param ctx the parse tree
	 */
	void enterThrowStatement(Java8Parser.ThrowStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#throwStatement}.
	 * @param ctx the parse tree
	 */
	void exitThrowStatement(Java8Parser.ThrowStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#synchronizedStatement}.
	 * @param ctx the parse tree
	 */
	void enterSynchronizedStatement(Java8Parser.SynchronizedStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#synchronizedStatement}.
	 * @param ctx the parse tree
	 */
	void exitSynchronizedStatement(Java8Parser.SynchronizedStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void enterTryStatement(Java8Parser.TryStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void exitTryStatement(Java8Parser.TryStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#catches}.
	 * @param ctx the parse tree
	 */
	void enterCatches(Java8Parser.CatchesContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#catches}.
	 * @param ctx the parse tree
	 */
	void exitCatches(Java8Parser.CatchesContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#catchClause}.
	 * @param ctx the parse tree
	 */
	void enterCatchClause(Java8Parser.CatchClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#catchClause}.
	 * @param ctx the parse tree
	 */
	void exitCatchClause(Java8Parser.CatchClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#catchFormalParameter}.
	 * @param ctx the parse tree
	 */
	void enterCatchFormalParameter(Java8Parser.CatchFormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#catchFormalParameter}.
	 * @param ctx the parse tree
	 */
	void exitCatchFormalParameter(Java8Parser.CatchFormalParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#catchType}.
	 * @param ctx the parse tree
	 */
	void enterCatchType(Java8Parser.CatchTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#catchType}.
	 * @param ctx the parse tree
	 */
	void exitCatchType(Java8Parser.CatchTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#finally_}.
	 * @param ctx the parse tree
	 */
	void enterFinally_(Java8Parser.Finally_Context ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#finally_}.
	 * @param ctx the parse tree
	 */
	void exitFinally_(Java8Parser.Finally_Context ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#tryWithResourcesStatement}.
	 * @param ctx the parse tree
	 */
	void enterTryWithResourcesStatement(Java8Parser.TryWithResourcesStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#tryWithResourcesStatement}.
	 * @param ctx the parse tree
	 */
	void exitTryWithResourcesStatement(Java8Parser.TryWithResourcesStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#resourceSpecification}.
	 * @param ctx the parse tree
	 */
	void enterResourceSpecification(Java8Parser.ResourceSpecificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#resourceSpecification}.
	 * @param ctx the parse tree
	 */
	void exitResourceSpecification(Java8Parser.ResourceSpecificationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#resourceList}.
	 * @param ctx the parse tree
	 */
	void enterResourceList(Java8Parser.ResourceListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#resourceList}.
	 * @param ctx the parse tree
	 */
	void exitResourceList(Java8Parser.ResourceListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#resource}.
	 * @param ctx the parse tree
	 */
	void enterResource(Java8Parser.ResourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#resource}.
	 * @param ctx the parse tree
	 */
	void exitResource(Java8Parser.ResourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(Java8Parser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(Java8Parser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#primaryNoNewArray}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryNoNewArray(Java8Parser.PrimaryNoNewArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primaryNoNewArray}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryNoNewArray(Java8Parser.PrimaryNoNewArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#primaryNoNewArray_lf_arrayAccess}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryNoNewArray_lf_arrayAccess(Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primaryNoNewArray_lf_arrayAccess}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryNoNewArray_lf_arrayAccess(Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#primaryNoNewArray_lfno_arrayAccess}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryNoNewArray_lfno_arrayAccess(Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primaryNoNewArray_lfno_arrayAccess}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryNoNewArray_lfno_arrayAccess(Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#primaryNoNewArray_lf_primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryNoNewArray_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primaryNoNewArray_lf_primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryNoNewArray_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#primaryNoNewArray_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryNoNewArray_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primaryNoNewArray_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryNoNewArray_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classInstanceCreationExpression}.
	 * @param ctx the parse tree
	 */
	void enterClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classInstanceCreationExpression}.
	 * @param ctx the parse tree
	 */
	void exitClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classInstanceCreationExpression_lf_primary}.
	 * @param ctx the parse tree
	 */
	void enterClassInstanceCreationExpression_lf_primary(Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classInstanceCreationExpression_lf_primary}.
	 * @param ctx the parse tree
	 */
	void exitClassInstanceCreationExpression_lf_primary(Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#classInstanceCreationExpression_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void enterClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#classInstanceCreationExpression_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void exitClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#typeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgumentsOrDiamond(Java8Parser.TypeArgumentsOrDiamondContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#typeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgumentsOrDiamond(Java8Parser.TypeArgumentsOrDiamondContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#fieldAccess}.
	 * @param ctx the parse tree
	 */
	void enterFieldAccess(Java8Parser.FieldAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#fieldAccess}.
	 * @param ctx the parse tree
	 */
	void exitFieldAccess(Java8Parser.FieldAccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#fieldAccess_lf_primary}.
	 * @param ctx the parse tree
	 */
	void enterFieldAccess_lf_primary(Java8Parser.FieldAccess_lf_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#fieldAccess_lf_primary}.
	 * @param ctx the parse tree
	 */
	void exitFieldAccess_lf_primary(Java8Parser.FieldAccess_lf_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#fieldAccess_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void enterFieldAccess_lfno_primary(Java8Parser.FieldAccess_lfno_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#fieldAccess_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void exitFieldAccess_lfno_primary(Java8Parser.FieldAccess_lfno_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#arrayAccess}.
	 * @param ctx the parse tree
	 */
	void enterArrayAccess(Java8Parser.ArrayAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#arrayAccess}.
	 * @param ctx the parse tree
	 */
	void exitArrayAccess(Java8Parser.ArrayAccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#arrayAccess_lf_primary}.
	 * @param ctx the parse tree
	 */
	void enterArrayAccess_lf_primary(Java8Parser.ArrayAccess_lf_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#arrayAccess_lf_primary}.
	 * @param ctx the parse tree
	 */
	void exitArrayAccess_lf_primary(Java8Parser.ArrayAccess_lf_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#arrayAccess_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void enterArrayAccess_lfno_primary(Java8Parser.ArrayAccess_lfno_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#arrayAccess_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void exitArrayAccess_lfno_primary(Java8Parser.ArrayAccess_lfno_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodInvocation}.
	 * @param ctx the parse tree
	 */
	void enterMethodInvocation(Java8Parser.MethodInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodInvocation}.
	 * @param ctx the parse tree
	 */
	void exitMethodInvocation(Java8Parser.MethodInvocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodInvocation_lf_primary}.
	 * @param ctx the parse tree
	 */
	void enterMethodInvocation_lf_primary(Java8Parser.MethodInvocation_lf_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodInvocation_lf_primary}.
	 * @param ctx the parse tree
	 */
	void exitMethodInvocation_lf_primary(Java8Parser.MethodInvocation_lf_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodInvocation_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void enterMethodInvocation_lfno_primary(Java8Parser.MethodInvocation_lfno_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodInvocation_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void exitMethodInvocation_lfno_primary(Java8Parser.MethodInvocation_lfno_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(Java8Parser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(Java8Parser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodReference}.
	 * @param ctx the parse tree
	 */
	void enterMethodReference(Java8Parser.MethodReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodReference}.
	 * @param ctx the parse tree
	 */
	void exitMethodReference(Java8Parser.MethodReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodReference_lf_primary}.
	 * @param ctx the parse tree
	 */
	void enterMethodReference_lf_primary(Java8Parser.MethodReference_lf_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodReference_lf_primary}.
	 * @param ctx the parse tree
	 */
	void exitMethodReference_lf_primary(Java8Parser.MethodReference_lf_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#methodReference_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void enterMethodReference_lfno_primary(Java8Parser.MethodReference_lfno_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#methodReference_lfno_primary}.
	 * @param ctx the parse tree
	 */
	void exitMethodReference_lfno_primary(Java8Parser.MethodReference_lfno_primaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#arrayCreationExpression}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreationExpression(Java8Parser.ArrayCreationExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#arrayCreationExpression}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreationExpression(Java8Parser.ArrayCreationExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#dimExprs}.
	 * @param ctx the parse tree
	 */
	void enterDimExprs(Java8Parser.DimExprsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#dimExprs}.
	 * @param ctx the parse tree
	 */
	void exitDimExprs(Java8Parser.DimExprsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#dimExpr}.
	 * @param ctx the parse tree
	 */
	void enterDimExpr(Java8Parser.DimExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#dimExpr}.
	 * @param ctx the parse tree
	 */
	void exitDimExpr(Java8Parser.DimExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void enterConstantExpression(Java8Parser.ConstantExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void exitConstantExpression(Java8Parser.ConstantExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(Java8Parser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(Java8Parser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#lambdaExpression}.
	 * @param ctx the parse tree
	 */
	void enterLambdaExpression(Java8Parser.LambdaExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#lambdaExpression}.
	 * @param ctx the parse tree
	 */
	void exitLambdaExpression(Java8Parser.LambdaExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#lambdaParameters}.
	 * @param ctx the parse tree
	 */
	void enterLambdaParameters(Java8Parser.LambdaParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#lambdaParameters}.
	 * @param ctx the parse tree
	 */
	void exitLambdaParameters(Java8Parser.LambdaParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#inferredFormalParameterList}.
	 * @param ctx the parse tree
	 */
	void enterInferredFormalParameterList(Java8Parser.InferredFormalParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#inferredFormalParameterList}.
	 * @param ctx the parse tree
	 */
	void exitInferredFormalParameterList(Java8Parser.InferredFormalParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#lambdaBody}.
	 * @param ctx the parse tree
	 */
	void enterLambdaBody(Java8Parser.LambdaBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#lambdaBody}.
	 * @param ctx the parse tree
	 */
	void exitLambdaBody(Java8Parser.LambdaBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression(Java8Parser.AssignmentExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression(Java8Parser.AssignmentExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(Java8Parser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(Java8Parser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#leftHandSide}.
	 * @param ctx the parse tree
	 */
	void enterLeftHandSide(Java8Parser.LeftHandSideContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#leftHandSide}.
	 * @param ctx the parse tree
	 */
	void exitLeftHandSide(Java8Parser.LeftHandSideContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator(Java8Parser.AssignmentOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator(Java8Parser.AssignmentOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression(Java8Parser.ConditionalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression(Java8Parser.ConditionalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalOrExpression(Java8Parser.ConditionalOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalOrExpression(Java8Parser.ConditionalOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalAndExpression(Java8Parser.ConditionalAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalAndExpression(Java8Parser.ConditionalAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#inclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterInclusiveOrExpression(Java8Parser.InclusiveOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#inclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitInclusiveOrExpression(Java8Parser.InclusiveOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#exclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterExclusiveOrExpression(Java8Parser.ExclusiveOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#exclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitExclusiveOrExpression(Java8Parser.ExclusiveOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#andExpression}.
	 * @param ctx the parse tree
	 */
	void enterAndExpression(Java8Parser.AndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#andExpression}.
	 * @param ctx the parse tree
	 */
	void exitAndExpression(Java8Parser.AndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(Java8Parser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(Java8Parser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(Java8Parser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(Java8Parser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void enterShiftExpression(Java8Parser.ShiftExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void exitShiftExpression(Java8Parser.ShiftExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(Java8Parser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(Java8Parser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(Java8Parser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(Java8Parser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(Java8Parser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(Java8Parser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#preIncrementExpression}.
	 * @param ctx the parse tree
	 */
	void enterPreIncrementExpression(Java8Parser.PreIncrementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#preIncrementExpression}.
	 * @param ctx the parse tree
	 */
	void exitPreIncrementExpression(Java8Parser.PreIncrementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#preDecrementExpression}.
	 * @param ctx the parse tree
	 */
	void enterPreDecrementExpression(Java8Parser.PreDecrementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#preDecrementExpression}.
	 * @param ctx the parse tree
	 */
	void exitPreDecrementExpression(Java8Parser.PreDecrementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#unaryExpressionNotPlusMinus}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpressionNotPlusMinus(Java8Parser.UnaryExpressionNotPlusMinusContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#unaryExpressionNotPlusMinus}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpressionNotPlusMinus(Java8Parser.UnaryExpressionNotPlusMinusContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpression(Java8Parser.PostfixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpression(Java8Parser.PostfixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#postIncrementExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostIncrementExpression(Java8Parser.PostIncrementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#postIncrementExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostIncrementExpression(Java8Parser.PostIncrementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#postIncrementExpression_lf_postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostIncrementExpression_lf_postfixExpression(Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#postIncrementExpression_lf_postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostIncrementExpression_lf_postfixExpression(Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#postDecrementExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostDecrementExpression(Java8Parser.PostDecrementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#postDecrementExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostDecrementExpression(Java8Parser.PostDecrementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#postDecrementExpression_lf_postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostDecrementExpression_lf_postfixExpression(Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#postDecrementExpression_lf_postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostDecrementExpression_lf_postfixExpression(Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java8Parser#castExpression}.
	 * @param ctx the parse tree
	 */
	void enterCastExpression(Java8Parser.CastExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java8Parser#castExpression}.
	 * @param ctx the parse tree
	 */
	void exitCastExpression(Java8Parser.CastExpressionContext ctx);
}