package com.informazine.forklift;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener
		extends JobExecutionListenerSupport
{

	private static final Logger log = LoggerFactory
			.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution)
	{
		if (jobExecution.getStatus() == BatchStatus.COMPLETED)
		{
			log.info("\n *** JOB FINISHED *** \n *** VALIDATE RESULTS ***");

			List<KnowledgeObject> results = jdbcTemplate.query(
					"SELECT product, object_type, query_topic_variant, full_solution, web_resource FROM vexss",
					new RowMapper<KnowledgeObject>()
					{
						@Override
						public KnowledgeObject mapRow(ResultSet rs, int row)
								throws SQLException
						{
							return new KnowledgeObject(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
						}
					});
 
			for (KnowledgeObject kbo : results)
			{
				log.info("Found <" + kbo + "> in the database.");
			}

		}
	}
}
