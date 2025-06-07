package org.feuyeux.ai.hello.repository;

import static org.feuyeux.ai.hello.utils.DotEnv.*;

import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.ai.qianfan.QianFanChatOptions;
import org.springframework.ai.qianfan.api.QianFanApi;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;

public class ModelClient {

  public static QianFanChatModel buildWenxinModel() {
    String[] tokenKeys = getQianfanTokenKeys();
    var qianFanApi = new QianFanApi(tokenKeys[0], tokenKeys[1]);
    var chatOptions =
        QianFanChatOptions.builder()
            .model(QianFanApi.ChatModel.ERNIE_Speed_8K.getValue())
            .temperature(0.7)
            .build();
    return new QianFanChatModel(qianFanApi, chatOptions);
  }

  public static ZhiPuAiChatModel buildZhiPuAiModel() {
    var zhiPuAiApi = new ZhiPuAiApi("https://open.bigmodel.cn/api/paas", getZhipuAiKey());
    var chatOptions = ZhiPuAiChatOptions.builder().model("GLM-4-Plus").temperature(0.7).build();
    return new ZhiPuAiChatModel(zhiPuAiApi, chatOptions);
  }

  //  public static ZhiPuAiChatModel buildModel(List<FunctionCallback> callbacks) {
  //    var zhiPuAiApi = new ZhiPuAiApi("https://open.bigmodel.cn/api/paas", getZhipuAiKey());
  //    var chatOptions =
  //        ZhiPuAiChatOptions.builder()
  //            .model("GLM-4-Plus")
  //            .temperature(0.7)
  //            .functionCallbacks(callbacks)
  //            .build();
  //    return new ZhiPuAiChatModel(zhiPuAiApi, chatOptions);
  //  }
}
