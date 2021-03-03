/*
 *
 *  *  Copyright (c) 2019-2020, 冷冷 (wangiegie@gmail.com).
 *  *  <p>
 *  *  Licensed under the GNU Lesser General Public License 3.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *  <p>
 *  * https://www.gnu.org/licenses/lgpl.html
 *  *  <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.river.common.core.util;

import com.river.common.core.constant.CommonConstants;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author lengleng
 */
@NoArgsConstructor //无参的构造方法
@AllArgsConstructor //添加一个构造函数，该构造函数含有所有已声明字段属性参数
@Accessors(chain = true) //配置getter和setter方法的生成结果 chain的中文含义是链式的，设置为true，则setter方法返回当前对象
@Data //生成Getter,Setter,equals,canEqual,hasCode,toString
public class Result<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private int code;
	private String message;
	private boolean isSuccess;

	private T data;

	private Object unknown;

	public static <T> Result<T> result(boolean isSuccess) {
		return result(null,isSuccess);
	}

	public static <T> Result<T> result(T data,boolean isSuccess) {
		return restResult(data, isSuccess?CommonConstants.SUCCESS:CommonConstants.FAIL,isSuccess, isSuccess?"success":"failed");
	}

	public static <T> Result<T> ok() {
		return restResult(null, CommonConstants.SUCCESS,true, "success");
	}

	public static <T> Result<T> ok(T data) {
		return restResult(data, CommonConstants.SUCCESS, true,"success");
	}

	public static <T> Result<T> ok(String message) {
		return ok(null,message);
	}

	public static <T> Result<T> ok(T data, String message) {
		return restResult(data, CommonConstants.SUCCESS,true, message);
	}

	public static <T> Result<T> failed() {
		return restResult(null, CommonConstants.FAIL, false,"failed");
	}

	public static <T> Result<T> failed(String message) {
		return restResult(null, CommonConstants.FAIL,false, message);
	}

	public static <T> Result<T> failed(T data) {
		return restResult(data, CommonConstants.FAIL, false,"failed");
	}

	public static <T> Result<T> failed(T data, String message) {
		return restResult(data, CommonConstants.FAIL,false, message);
	}

	public static <T> Result<T> restResult(T data, int code,boolean isSuccess, String message) {
		Result<T> apiResult = new Result<T>();
		apiResult.setCode(code);
		apiResult.setMessage(message);
		apiResult.setSuccess(isSuccess);
		apiResult.setData(data);
		return apiResult;
	}

}
