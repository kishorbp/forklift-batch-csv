package com.informazine.forklift;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

/*
 * This class is Optional. It is for Transformation logic, which I experimented with in the original Spring Guide
 * but in the updated scenario, I just want to pump the data from N csv files into the database. I have left it
 * active and used by default in this test case, as I will likely return to it so want the reference code. I may
 * switch to Spring Integration but this was just an experiment with Spring Batch.
 */

public class KnowledgeObjectItemProcessor implements ItemProcessor<KnowledgeObject, KnowledgeObject>
{

	private static final Logger log = LoggerFactory
			.getLogger(KnowledgeObjectItemProcessor.class);

	@Override
	public KnowledgeObject process(final KnowledgeObject kbo) throws Exception
	{
		final String product = kbo.getProduct();
		final String objType = kbo.getObjType();
		final String queryTopicVariant = kbo.getQueryTopicVariant();
		final String fullSolution = kbo.getFullSolution();
		final String webResource = kbo.getWebResource();

		final KnowledgeObject transformedKnowledgeObject = new KnowledgeObject(product, objType, queryTopicVariant, fullSolution, webResource);

		log.info(
				"Converting (" + kbo + ") into (" + transformedKnowledgeObject + ")");

		return transformedKnowledgeObject;
	}

}
