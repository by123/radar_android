package com.brotherhood.o2o.request;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.GoogleResponseResult;
import com.brotherhood.o2o.bean.nearby.OverseaPoi;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnGooglePoiResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;

import java.util.List;

/**
 * Created by jl.zhang on 2016/1/4.
 * https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters
 * 其中，output 可以是以下值之一：json（推荐）、xml
 * 系统要求提供特定参数才能发起附近地点搜索请求。依照 URL 的标准，所有参数都使用“与”字符 (&) 分隔。
 * 必填参数
 * key — 您的应用的 API 密钥。此密钥可以标识您的应用，以便进行配额管理。这样，通过应用添加的地点可立即供您的应用使用。请访问 Google Developers Console，创建 API 项目并获取您的密钥。
 * location — 检索地点信息所围绕的纬度/经度。必须指定为纬度、经度。
 * radius — 定义返回地点结果的范围（以米为单位）。所允许的最大半径为 50000 米。请注意，如果 rankby=distance（见下面可选参数部分中的描述）已指定，则不得包含 radius。
 * 如果 rankby=distance（见下面可选参数部分中的描述）已指定，那么需要提供 keyword、name 或 types 中的一个或多个参数。
 * 可选参数
 * keyword — 与 Google 为此地点编入索引的所有内容进行匹配的词语，包括但不仅限于名称、类型和地址，以及客户评论和其他第三方内容。
 * language — 语言代码，表示返回结果所应使用的语言（如提供该语言的话）。请参阅支持的语言列表及其代码。请注意，我们会经常更新支持的语言，因此，此列表可能并不全面。
 * minprice 和 maxprice（可选）— 将结果仅限制为指定范围内的地点。有效值的范围在 0（最实惠）和 4（最昂贵）之间，包括 0 和 4 本身。特定值所表示的准确数量因区域而异。
 * name — 与地点名称匹配的一个或多个项，以空格分隔。结果将限制为包含所传递的 name 值的项。请注意，地点可能有相关的其他名称，这些名称可能超出其列出的范围。API 尝试将所传递的名称值与所有名称进行匹配。因此，结果中可能返回地点，其列出的名称与搜索词不匹配，但关联的名称与搜索项匹配。
 * opennow — 仅返回发送查询时营业的地点。如果您在查询中包含此参数，就不会返回在 Google Places 数据库中未指定开放时间的地点。
 * rankby — 指定结果列出的顺序。可能的值为：
 *     prominence（默认）。此选项根据重要性对结果排序。优先列出指定区域的知名地点。知名度受 Google 索引中地点排序、全球知名度和其他因素影响。
 *     distance。此选项按其与指定的 location 之间的距离以升序对结果排序。当指定 distance 时，需要提供 keyword、name 或 types 中的一个或多个参数。
 * types — 将结果限制为至少与指定类型之一匹配的地点。类型应该用管道符号进行分隔 (type1|type2|etc)。请参阅支持的类型列表。
 * pagetoken — 返回上次所运行的搜索的后续 20 个结果。设置 pagetoken 参数将用上次使用的同一参数执行搜索 — 将忽略除 pagetoken 之外的所有参数。
 * zagatselected — 添加此参数（只是参数名，无关联值），以将您的搜索限制为 Zagat 精选商家位置。此参数不得包含 true 或 false 值。zagatselected 参数是试验性的，并且只向 Google Places API for Work 客户提供。
 *
 *
 * https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&types=food&name=cruise&key=AddYourOwnKeyHere
 */
public class OverseaNearbyBuildingRequest extends BaseAppRequest<GoogleResponseResult<List<OverseaPoi>>> {

    public OverseaNearbyBuildingRequest(String url, OnBaseResponseListener<GoogleResponseResult<List<OverseaPoi>>> baseResponseListener) {
        super(url, Method.GET, false, null, baseResponseListener);
    }

    /**
     *    https://maps.googleapis.com/maps/api/place/nearbysearch/json
     *      ?key=AIzaSyDnvT8TFLxR-QC0JGf0ZCQN26-OtIDziOQ
     *      &location=23,103
     *      &radius=500
     */
    public static OverseaNearbyBuildingRequest createOverseaNearbyRequest(String location, int radius,OnGooglePoiResponseListener<List<OverseaPoi>> listener){
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key="+ NearApplication.mInstance.getString(R.string.google_place_key)
                +"&location=" + location
                +"radius=" + radius;
        return new OverseaNearbyBuildingRequest(url, listener);
    }
}
