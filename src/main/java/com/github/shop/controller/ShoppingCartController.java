package com.github.shop.controller;

import com.github.shop.entity.PageResponse;
import com.github.shop.entity.Response;
import com.github.shop.entity.ShoppingCartData;
import com.github.shop.exception.BadRequestException;
import com.github.shop.service.ShoppingCartService;
import com.github.shop.service.UserContext;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ShoppingCartController {
    private static Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);
    
    private final ShoppingCartService shoppingCartService;
    
    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }
    // @formatter:off
    /**
     * @api {get} /shoppingCart 获取当前用户名下的所有购物车物品
     * @apiName GetShoppingCart
     * @apiGroup 购物车
     *
     * @apiHeader {String} Accept application/json
     *
     * @apiParam {Number} pageNum 页数，从1开始
     * @apiParam {Number} pageSize 每页显示的数量
     *
     * @apiSuccess {Number} pageNum 页数，从1开始
     * @apiSuccess {Number} pageSize 每页显示的数量
     * @apiSuccess {Number} totalPage 共有多少页
     * @apiSuccess {ShoppingCart} data 购物车物品列表，按照店铺分组
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "pageNum": 1,
     *       "pageSize": 10,
     *       "totalPage": 5,
     *       "data": [
     *         {
     *           "shop": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *            },
     *            "goods": [
     *              {
     *                  "id": 12345,
     *                  "name": "肥皂",
     *                  "description": "纯天然无污染肥皂",
     *                  "details": "这是一块好肥皂",
     *                  "imgUrl": "https://img.url",
     *                  "price": 500,
     *                  "number": 10,
     *                  "createdAt": "2020-03-22T13:22:03Z",
     *                  "updatedAt": "2020-03-22T13:22:03Z"
     *              },
     *              {
     *                    ...
     *              }
     *           ]
     *         }
     *       ]
     *     }
     *
     * @apiError 401 Unauthorized 若用户未登录
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    /**
     * @param pageNum  页码
     * @param pageSize 每页元素数量
     * @return 结果
     */
    // @formatter:on
    @GetMapping("/shoppingCart")
    public PageResponse<ShoppingCartData> getShoppingCart(
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize) {
        if (pageNum < 1 || pageSize < 1) {
            throw new BadRequestException("请求参数不合法");
        }
        return shoppingCartService.getShoppingCartOfUser(UserContext.getUser().getId(),
                pageNum,
                pageSize);
    }
    
    
    // @formatter:off
    /**
     * @api {post} /shoppingCart 加购物车
     * @apiName AddShoppingCart
     * @apiGroup 购物车
     *
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/json
     * @apiParamExample {json} Request-Example:
     *            {
     *              "goods": [
     *                {
     *                    "id": 12345,
     *                    "number": 10,
     *                },
     *                {
     *                    ...
     *                }
     *            }
     *
     * @apiSuccess {ShoppingCart} data 更新后的该店铺物品列表
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "data": {
     *           "shop": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *            },
     *            "goods": [
     *              {
     *                  "id": 12345,
     *                  "name": "肥皂",
     *                  "description": "纯天然无污染肥皂",
     *                  "details": "这是一块好肥皂",
     *                  "imgUrl": "https://img.url",
     *                  "address": "XXX",
     *                  "price": 500,
     *                  "number": 10,
     *                  "createdAt": "2020-03-22T13:22:03Z",
     *                  "updatedAt": "2020-03-22T13:22:03Z"
     *              },
     *              {
     *                    ...
     *              }
     *           ]
     *         }
     *       }
     *     }
     *
     * @apiError 400 Bad Request 若用户的请求中包含错误
     * @apiError 401 Unauthorized 若用户未登录
     * @apiError 404 Not Found 若商品未找到
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    /**
     * add goods to shopping cart
     *
     * @param goodsInfo 要添加到购物车的商品
     * @return shoppingCartInfo
     */
    // @formatter:on
    @PostMapping("/shoppingCart")
    public Response<ShoppingCartData> addToShoppingCart(@RequestBody ShoppingCartInfo goodsInfo) {
        return null;
    }
    
    public static class ShoppingCartInfo {
        private List<ShoppingCartItem> goods;
        
        public ShoppingCartInfo() {
        }
        
        public List<ShoppingCartItem> getGoods() {
            return goods;
        }
        
        public void setGoods(List<ShoppingCartItem> goods) {
            this.goods = goods;
        }
    }
    
    public static class ShoppingCartItem {
        private int number;
        private Long goodsId;
        
        public ShoppingCartItem() {
        }
        
        public int getNumber() {
            return number;
        }
        
        public void setNumber(int number) {
            this.number = number;
        }
        
        public Long getGoodsId() {
            return goodsId;
        }
        
        public void setGoodsId(Long goodsId) {
            this.goodsId = goodsId;
        }
    }
    
    
    // @formatter:off
    /**
     * @api {delete} /shoppingCart/:goodsId 删除当前用户购物车中指定的商品
     * @apiName DeleteShoppingCart
     * @apiGroup 购物车
     *
     * @apiHeader {String} Accept application/json
     *
     * @apiParam {Number} goodsId 要删除的商品ID
     *
     * @apiSuccess {ShoppingCart} data 更新后的该店铺物品列表
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "data": {
     *           "shop": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *            },
     *            "goods": [
     *              {
     *                  "id": 12345,
     *                  "name": "肥皂",
     *                  "description": "纯天然无污染肥皂",
     *                  "details": "这是一块好肥皂",
     *                  "imgUrl": "https://img.url",
     *                  "address": "XXX",
     *                  "price": 500,
     *                  "number": 10,
     *                  "createdAt": "2020-03-22T13:22:03Z",
     *                  "updatedAt": "2020-03-22T13:22:03Z"
     *              },
     *              {
     *                    ...
     *              }
     *           ]
     *         }
     *       }
     *     }
     *
     * @apiError 401 Unauthorized 若用户未登录
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    // @formatter:on
    
    /**
     * @param goodsId 要删除的商品id
     * @return 更新后的该店铺数据
     */
    @DeleteMapping("/shoppingCart/{id}")
    public Response<ShoppingCartData> deleteGoodsInShoppingCart(@PathVariable("id") Long goodsId) {
        return Response.ofData(shoppingCartService.deleteShoppingCart(goodsId));
    }
}
