一、创建订单测试用例:
http://localhost:8088/demo-service/v1/demo/order/create

1. SKU.ID = S2商品库存不足
{
  "uuid": "2bf15542-4e97-4961-b",
  "billNumber": "b3baea88-bab9-412e-9",
  "buyer": "购买人",
  "amount": 5000,
  "deliverType": "SELFPICK",
  "remark": "订单说明",
  "lines": [
    {
      "uuid": "7bff5874-6553-410d-9",
      "orderUuid": "2bf15542-4e97-4961-b",
      "lineNo": 0,
      "skuId": "S2",
      "skuName": "S2_Name",
      "qty": 20,
      "amount": 100000
    },
   {
      "uuid": "8bff5874-6553-410d-9",
      "orderUuid": "2bf15542-4e97-4961-b",
      "lineNo": 0,
      "skuId": "S1",
      "skuName": "S1_Name",
      "qty": 20,
      "amount": 100000
    }
  ]
}

2. 正常订单 (将SKU.ID= S2 的 qty改为10)
3. 订单重复创建

二、保存订单测试用例
http://localhost:8088/demo-service/v1/demo/order/update

1. 订单号(billNumber)不存在
2. 商品库存不足
3. 订单数量为商品库存最大值
3. 订单状态为已审核或已作废 (审核/作废 订单后再保存此订单)

三、审核订单测试用例
http://localhost:8088/demo-service/v1/demo/order/check/b3baea88-bab9-412e-9

1. 订单号不存在
2. 审核 已审核/已作废 的订单

四、作废订单测试用例
http://localhost:8088/demo-service/v1/demo/order/abort/b3baea88-bab9-412e-9

1. 订单号不存在
2. 作废 已作废订单
3. 作废已提交订单
4. 作废已审核订单

五、附
1. 创建、保存 SKU.ID不存在的订单