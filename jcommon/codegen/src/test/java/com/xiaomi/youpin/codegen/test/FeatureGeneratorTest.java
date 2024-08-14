package com.xiaomi.youpin.codegen.test;

import org.junit.Test;
import run.mone.ai.codegen.FeatureGenerator;
import run.mone.ai.codegen.MybatisGenerator;
import run.mone.ai.codegen.bo.FeatureGeneratType;
import run.mone.ai.codegen.bo.FeatureGenerateBo;

public class FeatureGeneratorTest {

	private String dbUrl = "";
	private String dbUser = "";
	private String dbPwd = "";

	@Test
	public void testCreateTable() {
	    FeatureGenerateBo featureGenerateBo = new FeatureGenerateBo();
	    featureGenerateBo.setType(FeatureGeneratType.TABLE);
	    featureGenerateBo.setJdbcUrl(dbUrl);
	    featureGenerateBo.setUserName(dbUser);
	    featureGenerateBo.setPassword(dbPwd);
		String sql = "CREATE TABLE IF NOT EXISTS `user` (\n" +
				"  `id` INT NOT NULL AUTO_INCREMENT,\n" +
				"  `name` VARCHAR(255) NOT NULL,\n" +
				"  `age` INT,\n" +
				"  PRIMARY KEY (`id`)\n" +
				");";
		featureGenerateBo.setSql(sql);
		FeatureGenerator.generateWithTemplate(featureGenerateBo);
	}

	@Test
	public void testGenerateWithMybatis() {
	    FeatureGenerateBo featureGenerateBo = new FeatureGenerateBo();
	    featureGenerateBo.setType(FeatureGeneratType.CODE_WITH_MYBATIS_GENERATOR);
		featureGenerateBo.setJdbcUrl(dbUrl);
		featureGenerateBo.setUserName(dbUser);
		featureGenerateBo.setPassword(dbPwd);
		featureGenerateBo.setTableName("user");

		featureGenerateBo.setMybatisDaoModule("/your/path/mone/jcommon/codegen");
		featureGenerateBo.setMybatisXMLPath("com.xiaomi.dao.mapper");
		featureGenerateBo.setMybatisDaoPath("com.xiaomi.dao.mapper");
		featureGenerateBo.setMybatisEntityPath("com.xiaomi.dao.entity");

		MybatisGenerator.generateMyBatisFiles(featureGenerateBo);
	}

}
