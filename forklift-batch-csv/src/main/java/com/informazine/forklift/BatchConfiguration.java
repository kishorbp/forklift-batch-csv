package com.informazine.forklift;

import java.io.IOException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration
{
	private static final Logger log = LoggerFactory
			.getLogger(BatchConfiguration.class);

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	// tag::readerwriterprocessor[]
	@Bean
	public MultiResourceItemReader<KnowledgeObject> reader()
	{
		// enable multiple file resource processing
		MultiResourceItemReader<KnowledgeObject> multiReader = new MultiResourceItemReader<KnowledgeObject>();
		PathMatchingResourcePatternResolver pathMatchinResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources;
		try
		{
			// http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/core/io/support/PathMatchingResourcePatternResolver.html
			resources = pathMatchinResolver.getResources("classpath:*.csv");
			multiReader.setResources(resources);
		}
		catch (IOException e)
		{
			log.error("Failed to create resource list. Check you are providing a valid classpath to the source files" + e);
		}

		FlatFileItemReader<KnowledgeObject> reader = new FlatFileItemReader<KnowledgeObject>();
		
		multiReader.setDelegate(reader);

		// If you flip back to single csv file, just remove the multiresourceitemreader and delegate and set this code active again
		//reader.setResource(new ClassPathResource("vexss-data.csv"));
		reader.setLineMapper(new DefaultLineMapper<KnowledgeObject>()
		{
			{
				setLineTokenizer(new DelimitedLineTokenizer()
				{
					{
						setNames(new String[]
						{ "product", "objType", "queryTopicVariant",
								"fullSolution", "webResource" });
					}
				});
				setFieldSetMapper(
						new BeanWrapperFieldSetMapper<KnowledgeObject>()
						{
							{
								setTargetType(KnowledgeObject.class);
							}
						});
			}
		});

		// added by niall guerin - app is opening too many files - spring guide
		// sample is not closing but the spring docs say resources
		// need to be closed. increasing os parameters is only treating the
		// symptoms. close the reader!
		reader.close();
		multiReader.close();
		
		return multiReader;
	}

	@Bean
	public KnowledgeObjectItemProcessor processor()
	{
		return new KnowledgeObjectItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<KnowledgeObject> writer()
	{
		JdbcBatchItemWriter<KnowledgeObject> writer = new JdbcBatchItemWriter<KnowledgeObject>();
		writer.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<KnowledgeObject>());
		writer.setSql(
				"INSERT INTO vexss (product, object_type, query_topic_variant, full_solution, web_resource) VALUES (:product, :objType, :queryTopicVariant, :fullSolution, :webResource)");
		writer.setDataSource(dataSource);
		return writer;
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener)
	{
		return jobBuilderFactory.get("importUserJob")
				.incrementer(new RunIdIncrementer()).listener(listener)
				.flow(step1()).end().build();
	}

	@Bean
	public Step step1()
	{
		return stepBuilderFactory.get("step1")
				.<KnowledgeObject, KnowledgeObject> chunk(10).reader(reader())
				.processor(processor()).writer(writer()).build();
	}
	// end::jobstep[]
}
