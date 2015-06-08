package com.baidu.themeanimation.net.sourceforge.jeval.function.math;

import java.util.ArrayList;

import com.baidu.themeanimation.net.sourceforge.jeval.EvaluationConstants;
import com.baidu.themeanimation.net.sourceforge.jeval.Evaluator;
import com.baidu.themeanimation.net.sourceforge.jeval.function.Function;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionConstants;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionException;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionHelper;
import com.baidu.themeanimation.net.sourceforge.jeval.function.FunctionResult;

/*
 * this function will return whether the first value is bigger than the second one
 */
public class Gt implements Function {

	public String getName() {
		return "gt";
	}

	public FunctionResult execute(Evaluator evaluator, String arguments)
			throws FunctionException {
		int result = 0;

		ArrayList numbers = FunctionHelper.getDoubles(arguments, 
				EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);

		if (numbers.size() != 2) {
			throw new FunctionException("Two numeric arguments are required.");
		}

		try {
			double argumentOne = ((Double) numbers.get(0)).doubleValue();
			double argumentTwo = ((Double) numbers.get(1)).doubleValue();
			if (argumentOne > argumentTwo) {
				result = 1;
			}
		} catch (Exception e) {
			throw new FunctionException("Two numeric arguments are required.", e);
		}

		return new FunctionResult(String.valueOf(result), 
				FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC);
	}

}
