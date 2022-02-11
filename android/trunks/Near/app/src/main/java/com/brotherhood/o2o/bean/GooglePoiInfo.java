package com.brotherhood.o2o.bean;

/**
 * Created by jl.zhang on 2016/1/4.
 * <p/>
 * <p/>
 * "address_components": [
 * {
 * "long_name": "807乡道",
 * "short_name": "Y807",
 * "types": [
 * "route"
 * ]
 * },
 * {
 * "long_name": "惠东县",
 * "short_name": "惠东县",
 * "types": [
 * "sublocality_level_1",
 * "sublocality",
 * "political"
 * ]
 * },
 * {
 * "long_name": "惠州",
 * "short_name": "惠州",
 * "types": [
 * "locality",
 * "political"
 * ]
 * },
 * {
 * "long_name": "广东省",
 * "short_name": "广东省",
 * "types": [
 * "administrative_area_level_1",
 * "political"
 * ]
 * },
 * {
 * "long_name": "中国",
 * "short_name": "CN",
 * "types": [
 * "country",
 * "political"
 * ]
 * }
 * ],
 * "formatted_address": "中国广东省惠州市惠东县807乡道",
 * "geometry": {
 * "bounds": {
 * "northeast": {
 * "lat": 23.02688,
 * "lng": 115.0046535
 * },
 * "southwest": {
 * "lat": 23.0228893,
 * "lng": 114.98824
 * }
 * },
 * "location": {
 * "lat": 23.0258195,
 * "lng": 114.9974153
 * },
 * "location_type": "GEOMETRIC_CENTER",
 * "viewport": {
 * "northeast": {
 * "lat": 23.02688,
 * "lng": 115.0046535
 * },
 * "southwest": {
 * "lat": 23.0228893,
 * "lng": 114.98824
 * }
 * }
 * },
 * "place_id": "ChIJ8QeXYJ4cBTQRsytiK3DIkrU",
 * "types": [
 * "route"
 * ]
 */
public class GooglePoiInfo {
    public String formatted_address;

}
