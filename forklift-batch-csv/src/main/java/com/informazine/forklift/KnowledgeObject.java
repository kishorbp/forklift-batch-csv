package com.informazine.forklift;

public class KnowledgeObject
{
	private String product;
	private String objType;
	private String queryTopicVariant;
	private String fullSolution;
	private String webResource;

	public KnowledgeObject()
	{
	}

	// default constructor
	public KnowledgeObject(String product, String objType,
			String queryTopicVariant, String fullSolution, String webResource)
	{
		this.product = product;
		this.objType = objType;
		this.queryTopicVariant = queryTopicVariant;
		this.fullSolution = fullSolution;
		this.webResource = webResource;
	}

	public String getProduct()
	{
		return product;
	}

	public void setProduct(String product)
	{
		this.product = product;
	}

	public String getObjType()
	{
		return objType;
	}

	public void setObjType(String objType)
	{
		this.objType = objType;
	}

	public String getQueryTopicVariant()
	{
		return queryTopicVariant;
	}

	public void setQueryTopicVariant(String queryTopicVariant)
	{
		this.queryTopicVariant = queryTopicVariant;
	}

	public String getFullSolution()
	{
		return fullSolution;
	}

	public void setFullSolution(String fullSolution)
	{
		this.fullSolution = fullSolution;
	}

	public String getWebResource()
	{
		return webResource;
	}

	public void setWebResource(String webResource)
	{
		this.webResource = webResource;
	}

	@Override
	public String toString()
	{
		return "KnowledgeObject [product=" + product + ", objType=" + objType
				+ ", queryTopicVariant=" + queryTopicVariant + ", fullSolution="
				+ fullSolution + ", webResource=" + webResource + "]";
	}
}
