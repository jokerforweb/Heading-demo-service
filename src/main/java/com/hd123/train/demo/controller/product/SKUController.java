package com.hd123.train.demo.controller.product;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.train.demo.dao.product.SKUDao;
import com.hd123.train.demo.infrastructure.biz.BaseResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Silent
 */
@RestController
@RequestMapping(value = "/v1/demo/sku", produces = "application/json;charset=utf-8")
public class SKUController {

  @Autowired
  private SKUDao skuDao;

  @ApiOperation(value = "创建商品")
  @PostMapping(path = "/create")
  public BaseResponse create(
          @ApiParam(value = "商品数据", required = true) @RequestBody SKU sku) {
    SKU target = skuDao.get(sku.getId());
    if (target != null) {
      return BaseResponse.fail(503, "指定ID的商品已存在");
    }
    skuDao.save(sku);
    return BaseResponse.success();
  }

  @ApiOperation(value = "更新商品资料")
  @PostMapping(path = "/update")
  public BaseResponse update(
          @ApiParam(value = "商品数据", required = true) @RequestBody SKU sku) {
    SKU target = skuDao.get(sku.getId());
    if (target == null) {
      return BaseResponse.fail(503, "商品数据不存在");
    }
    target.setName(sku.getName());
    target.setRemark(sku.getRemark());
    target.setPrice(sku.getPrice());
    target.setMarketPrice(sku.getMarketPrice());
    target.setProperties(sku.getProperties());
    target.setStockQty(sku.getStockQty());
    target.setDescriptions(sku.getDescriptions());
    target.setImage(sku.getImage());
    target.setImages(sku.getImages());
    target.setTags(sku.getTags());
    skuDao.save(sku);
    return BaseResponse.success();
  }

  @ApiOperation(value = "获取指定SKU")
  @GetMapping(path = "/{id}")
  public BaseResponse<SKU> get(
          @ApiParam(value = "商品ID", required = true) @PathVariable("id") String id) {
    SKU sku = skuDao.get(id);
    return BaseResponse.success(sku);
  }

  @ApiOperation(value = "删除指定SKU")
  @DeleteMapping(path = "/{id}")
  public BaseResponse delete(
          @ApiParam(value = "商品ID", required = true) @PathVariable("id") String id) {
    skuDao.delete(id);
    return BaseResponse.success();
  }

  @ApiOperation(value = "根据查询条件查询SKU")
  @PostMapping(path = "/query")
  public BaseResponse<QueryResult<SKU>> query(
          @ApiParam(value = "商品过滤条件", required = true) @RequestBody SKUFilter filter) {
    QueryResult queryResult = skuDao.query(filter);
    return BaseResponse.success(queryResult);
  }
}
