package com.github.shop.controller;

import com.github.shop.entity.PageResponse;
import com.github.shop.entity.Response;
import com.github.shop.exception.BadRequestException;
import com.github.shop.generate.Shop;
import com.github.shop.service.ShopService;
import com.github.shop.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;

@RestController
@RequestMapping("/api/v1")
public class ShopController {
    private ShopService shopService;
    
    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }
    
    /**
     * @api {post} /shop 创建店铺
     * @apiName CreateShop
     * @apiGroup 店铺
     *
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/json
     * @apiParamExample {json} Request-Example:
     *          {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *          }
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 201 Created
     *     {
     *       "data": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *       }
     *     }
     *
     * @apiError 400 Bad Request 若用户的请求中包含错误
     * @apiError 401 Unauthorized 若用户未登录
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    /**
     * create shop.
     *
     * @param shop
     * @return shop created
     */
    @PostMapping("/shop")
    public Response<Shop> createShop(@RequestBody Shop shop, HttpServletResponse response) {
        shop.setOwnerUserId(UserContext.getUser().getId());
        Shop createdShop = shopService.createShop(shop);
        response.setStatus(SC_CREATED);
        return Response.ofData(createdShop);
    }
    
    /**
     * @api {DELETE} /shop/:id 删除店铺
     * @apiName DeleteShop
     * @apiGroup 店铺
     *
     * @apiHeader {String} Accept application/json
     *
     * @apiParam {Number} id 店铺ID
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 204 No Content
     *     {
     *       "data": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *       }
     *     }
     *
     * @apiError 400 Bad Request 若用户的请求中包含错误
     * @apiError 404 Not Found 若店铺未找到
     * @apiError 401 Unauthorized 若用户未登录
     * @apiError 403 Forbidden 若用户尝试删除非自己管理店铺
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    /**
     * delete shop.
     *
     * @param shopId
     * @return
     */
    @DeleteMapping("/shop/{id}")
    public Response<Shop> deleteShop(@PathVariable("id") Long shopId, HttpServletResponse response) {
        if (shopId == null || shopId < 1) {
            throw new BadRequestException("删除参数不合法");
        }
        
        Shop shop = shopService.deleteShop(shopId);
        response.setStatus(SC_NO_CONTENT);
        return Response.ofData(shop);
    }
    
    /**
     * @api {get} /shop/:id 获取指定ID的店铺
     * @apiName GetShopById
     * @apiGroup 店铺
     *
     * @apiHeader {String} Accept application/json
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 Created
     *     {
     *       "data": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *       }
     *     }
     *
     * @apiError 401 Unauthorized 若用户未登录
     * @apiError 404 Not found 若店铺未找到
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    /**
     * get shop info by shop id.
     *
     * @param shopId
     * @return shop info
     */
    @GetMapping("/shop/{id}")
    public Response<Shop> getShopById(@PathVariable("id") Long shopId) {
        if (shopId == null || shopId < 1) {
            throw new BadRequestException("请求商店id不合法");
        }
        Shop shop = shopService.getShopById(shopId);
        return Response.ofData(shop);
    }
    
    /**
     * @api {get} /shop 获取当前用户名下的所有店铺
     * @apiName GetShop
     * @apiGroup 店铺
     * @apiHeader {String} Accept application/json
     * @apiParam {Number} pageNum 页数，从1开始
     * @apiParam {Number} pageSize 每页显示的数量
     * @apiSuccess {Number} pageNum 页数，从1开始
     * @apiSuccess {Number} pageSize 每页显示的数量
     * @apiSuccess {Number} totalPage 共有多少页
     * @apiSuccess {Shop} data 店铺列表
     * @apiSuccessExample Success-Response:
     *         HTTP/1.1 200 OK
     *         {
     *         "pageNum": 1,
     *         "pageSize": 10,
     *         "totalPage": 5,
     *         "data": [
     *         {
     *         "id": 12345,
     *         "name": "我的店铺",
     *         "description": "我的苹果专卖店",
     *         "imgUrl": "https://img.url",
     *         "ownerUserId": 12345,
     *         "createdAt": "2020-03-22T13:22:03Z",
     *         "updatedAt": "2020-03-22T13:22:03Z"
     *         },
     *         {
     *         ...
     *         }
     *         ]
     *         }
     * @apiError 401 Unauthorized 若用户未登录
     * @apiErrorExample Error-Response:
     *         HTTP/1.1 401 Unauthorized
     *         {
     *         "message": "Unauthorized"
     *         }
     */
    /**
     * get shops by page.
     *
     * @param pageSize
     * @param pageNum
     * @return 店铺列表
     */
    @GetMapping("/shop")
    public PageResponse<Shop> getShops(@RequestParam("pageSize") int pageSize,
                                       @RequestParam("pageNum") int pageNum) {
        if (pageSize < 1 || pageNum < 1) {
            throw new BadRequestException("请求参数不合法");
        }
        return shopService.getShopsByPage(pageSize, pageNum);
    }
    
    /**
     * @api {PATCH} /shop/:id 修改店铺
     * @apiName UpdateShop
     * @apiGroup 店铺
     *
     * @apiParam {Number} id 店铺ID
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/json
     * @apiParamExample {json} Request-Example:
     *          {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *          }
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "data": {
     *              "id": 12345,
     *              "name": "我的店铺",
     *              "description": "我的苹果专卖店",
     *              "imgUrl": "https://img.url",
     *              "ownerUserId": 12345,
     *              "createdAt": "2020-03-22T13:22:03Z",
     *              "updatedAt": "2020-03-22T13:22:03Z"
     *       }
     *     }
     *
     * @apiError 400 Bad Request 若用户的请求中包含错误
     * @apiError 404 Not Found 若店铺未找到
     * @apiError 401 Unauthorized 若用户未登录
     * @apiError 403 Forbidden 若用户尝试修改非自己管理店铺
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    /**
     * update shop.
     *
     * @param shopId
     * @param shop
     * @return update response
     */
    @RequestMapping(value = "/shop/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public Response<Shop> updateShop(@PathVariable("id") Long shopId,
                                     @RequestBody Shop shop) {
        Shop deletedShop = shopService.updateShop(shopId, shop);
        return Response.ofData(deletedShop);
    }
    
}
