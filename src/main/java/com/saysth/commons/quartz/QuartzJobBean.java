/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.saysth.commons.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * Simple implementation of the Quartz Job interface, applying the passed-in
 * JobDataMap and also the SchedulerContext as bean property values. This is
 * appropriate because a new Job instance will be created for each execution.
 * JobDataMap entries will override SchedulerContext entries with the same keys.
 * 
 * <p>
 * For example, let's assume that the JobDataMap contains a key "myParam" with
 * value "5": The Job implementation can then expose a bean property "myParam"
 * of type int to receive such a value, i.e. a method "setMyParam(int)". This
 * will also work for complex types like business objects etc.
 * 
 * <p>
 * Note: The QuartzJobBean class itself only implements the standard Quartz
 * {@link org.quartz.Job} interface. Let your subclass explicitly implement the
 * Quartz {@link org.quartz.StatefulJob} interface to mark your concrete job
 * bean as stateful.
 * 
 * <p>
 * This version of QuartzJobBean requires Quartz 1.5 or higher, due to the
 * support for trigger-specific job data.
 * 
 * <p>
 * <b>Note that as of Spring 2.0 and Quartz 1.5, the preferred way to apply
 * dependency injection to Job instances is via a JobFactory:</b> that is, to
 * specify {@link SpringBeanJobFactory} as Quartz JobFactory (typically via
 * {@link SchedulerFactoryBean#setJobFactory} SchedulerFactoryBean's
 * "jobFactory" property}). This allows to implement dependency-injected Quartz
 * Jobs without a dependency on Spring base classes.
 * 
 * @author Juergen Hoeller
 * @since 18.02.2004
 * @see org.quartz.JobExecutionContext#getMergedJobDataMap()
 * @see org.quartz.Scheduler#getContext()
 * @see JobDetailBean#setJobDataAsMap
 * @see SimpleTriggerBean#setJobDataAsMap
 * @see CronTriggerBean#setJobDataAsMap
 * @see SchedulerFactoryBean#setSchedulerContextAsMap
 * @see SpringBeanJobFactory
 * @see SchedulerFactoryBean#setJobFactory
 */
public abstract class QuartzJobBean implements Job {

	/**
	 * This implementation applies the passed-in job data map as bean property
	 * values, and delegates to <code>executeInternal</code> afterwards.
	 * 
	 * @see #executeInternal
	 */
	public final void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValues(context.getScheduler().getContext());
			pvs.addPropertyValues(context.getMergedJobDataMap());
			bw.setPropertyValues(pvs, true);
		} catch (SchedulerException ex) {
			throw new JobExecutionException(ex);
		}
		executeInternal(context);
	}

	/**
	 * Execute the actual job. The job data map will already have been applied
	 * as bean property values by execute. The contract is exactly the same as
	 * for the standard Quartz execute method.
	 * 
	 * @see #execute
	 */
	protected abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;

}