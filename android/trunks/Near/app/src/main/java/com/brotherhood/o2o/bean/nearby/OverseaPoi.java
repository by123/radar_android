package com.brotherhood.o2o.bean.nearby;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by jl.zhang on 2016/1/4.
 * <p/>
 * "geometry": {
     * "location": {
         * "lat": 23.219932,
         * "lng": 102.835223
     * },
     * "viewport": {
         * "northeast": {
         * "lat": 23.3077927,
         * "lng": 103.2214827
         * },
         * "southwest": {
         * "lat": 22.824635,
         * "lng": 102.4524806
         * }
     * }
 * },
 * "icon": "https://maps.gstatic.com/mapfiles/place_api/icons/geocode-71.png",
 * "id": "577d2388bf04c012de46685c83de673faf14ff79",
 * "name": "元阳县",
 * "place_id": "ChIJ5cqpz0tN0jYR9cYWzWnSkAk",
 * "reference": "CqQBlAAAAMieR1lhvDFVxp0QjP4izfV6W4TijMKcFxhF-qhW9eK-0ZrNVewtd9Q8H2Gdt9OKKCVi_gYUbaBvPI9BxF3CRdEf95qXHtSB0a37_zdzbO8-VOf1EOS_CUHfJKtzc8Lu1SljSvWJeNAqnzknUS3QGLvIUXLwHu9ZRNiUCFEkZaj7xq4sFXs8xJlULwi9odEkobyeiA0ozOjUkvpLd9dDgKgSEFhnUZJZNFfR1JKuoKhs5AQaFEF-RYNR2mB9XeIK-3bZmlhysYMt",
 * "scope": "GOOGLE",
 * "types": [
 * "sublocality_level_1",
 * "sublocality",
 * "political"
 * ],
 * "vicinity": "元阳县"
 */
public class OverseaPoi {

    public String icon;
    public String name;
    @JSONField(name = "vicinity")
    public String nearBy;

    @JSONField(name = "geometry")
    public OverseaGeo geoInfo;

    public String reference;
}
