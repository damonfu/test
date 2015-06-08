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
 * this function will judge whether the 2 value is the same 
 */
public class Paras implements Function{

	public String getName() {
		return "paras";
	}

	public FunctionResult execute(final Evaluator evaluator, final String arguments)
			throws FunctionException {
		String result = null;
		
		ArrayList numbers = FunctionHelper.getDoubles(arguments, 
				EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);

		int count = numbers.size();
		if (count > 0){
			result = String.valueOf(numbers.get(0));
			for (int i = 1; i < count; i++) {
				result += ","+String.valueOf(numbers.get(i));
			} 
		}
		
		return new FunctionResult(String.valueOf(result), 
				FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
	}

}
