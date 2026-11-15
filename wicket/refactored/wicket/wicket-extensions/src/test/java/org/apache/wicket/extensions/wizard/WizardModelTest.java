/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.extensions.wizard;

import java.util.Iterator;

import org.apache.wicket.extensions.wizard.WizardModel.ICondition;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;

/**
 * Test for {@link WizardModel}.
 */
public class WizardModelTest extends WicketTestCase
{

	/**
	 * Test steps are initialized correctly.
	 */
	@Test
	public void testResetInitsSteps()
	{
		WizardModel model = new WizardModel();
		model.add(new WizardStep());
		model.add(new WizardStep());
		model.add(new WizardStep());
		model.reset();

		Iterator<IWizardStep> iterator = model.stepIterator();
		assertNotNull(iterator, () -> "Add assertion message");

		while (iterator.hasNext())
		{
			WizardStep step = (WizardStep)iterator.next();
			assertEquals(model, step.getWizardModel(), () -> "Add assertion message");
		}
	}
	
	@Test
	public void testWizard()
	{
		WizardModel model = new WizardModel();
		
		WizardStep step1 = new WizardStep();
		model.add(step1);
		
		WizardStep step2 = new WizardStep();
		model.add(step2, () -> false);
		
		class ConditionWizadStep extends WizardStep implements ICondition {

			@Override
			public boolean evaluate()
			{
				return false;
			}
			
		};
		WizardStep step3 = new ConditionWizadStep();
		model.add(step3);
		
		WizardStep step4 = new WizardStep();
		model.add(step4);

		model.reset();
		
		Iterator<IWizardStep> iterator = model.stepIterator();
		assertTrue(iterator.hasNext(), () -> "Add assertion message");
		iterator.next();
		// Line removed due to Duplicate Assert: assertTrue(iterator.hasNext(), () -> "Add assertion message");
		iterator.next();
		assertFalse(iterator.hasNext(), () -> "Add assertion message");
		try {
			iterator.next();
			fail();
		} catch (Exception expected) {
		}

		assertSame(step1, model.getActiveStep(), () -> "Add assertion message");
		assertTrue(model.isNextAvailable(), () -> "Add assertion message");
		assertFalse(model.isLastStep(model.getActiveStep()), () -> "Add assertion message");
		assertFalse(model.isFinishAvailable(), () -> "Add assertion message");

		model.next();
		assertSame(step4, model.getActiveStep(), () -> "Add assertion message");
		assertFalse(model.isNextAvailable(), () -> "Add assertion message");
		assertTrue(model.isLastStep(model.getActiveStep()), () -> "Add assertion message");
		assertTrue(model.isFinishAvailable(), () -> "Add assertion message");

		try {
			model.next();
			// Line removed due to Duplicate Assert: fail();
		} catch (Exception expected) {
		}
	}
}