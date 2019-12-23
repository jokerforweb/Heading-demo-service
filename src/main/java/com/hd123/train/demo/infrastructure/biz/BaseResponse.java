package com.hd123.train.demo.infrastructure.biz;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseResponse<T> {
  @ApiModelProperty(value = "执行结果")
  private boolean success = true;
  @ApiModelProperty(value = "响应吗")
  private int echoCode = 0;
  @ApiModelProperty(value = "响应消息")
  private String echoMessage;
  @ApiModelProperty(value = "响应数据")
  private T data;

  public static BaseResponse success() {
    return new BaseResponse();
  }

  public static <T> BaseResponse<T> success(T data) {
    BaseResponse<T> response = new BaseResponse();
    response.setData(data);
    return response;
  }

  public static BaseResponse fail(int echoCode, String echoMessage) {
    BaseResponse response = new BaseResponse();
    response.setSuccess(false);
    response.setEchoCode(echoCode);
    response.setEchoMessage(echoMessage);
    return response;
  }


}
