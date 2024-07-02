package com.example.seldoc

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

// XML Response for Drug Info
@Xml(name = "response")
data class XmlResponse(
    @Element
    val body: myXmlBody
)

@Xml(name = "body")
data class myXmlBody(
    @Element
    val items: myXmlItems
)

@Xml(name = "items")
data class myXmlItems(
    @Element
    val item: MutableList<myXmlItem>
)

@Xml(name = "item")
data class myXmlItem(
    @PropertyElement
    val itemName: String?, // 제품명
    @PropertyElement
    val entpName: String?, // 업체명
    @PropertyElement
    val efcyQesitm: String?, // 효능
    @PropertyElement
    val useMethodQesitm: String?, // 사용법
    @PropertyElement
    val atpnQesitm: String?, // 주의사항
    @PropertyElement
    val atpnWarnQesitm: String?, // 주의사항 경고
    @PropertyElement
    val seQesitm: String?, // 부작용
    @PropertyElement
    val intrcQesitm: String?, // 상호작용
    @PropertyElement
    val depositMethodQesitm: String?, // 보관법
    @PropertyElement
    val itemImage:String?
) {
    constructor() : this(null, null, null, null, null, null, null, null, null, null)
}

// XML Response for Pharmacy Info
@Xml(name = "response")
data class PharmacyResponse(
    @Element
    val body: PharmacyBody
)

@Xml(name = "body")
data class PharmacyBody(
    @Element
    val items: PharmacyItems
)

@Xml(name = "items")
data class PharmacyItems(
    @Element
    val item: MutableList<PharmacyItem>
)

@Xml(name = "item")
data class PharmacyItem(
    @PropertyElement
    val yadmNm: String?, // 병원 이름
    @PropertyElement
    val addr: String?, // 주소
    @PropertyElement
    val clCdNm: String?, // 클리닉 코드 이름
    @PropertyElement
    val telno: String?, // 전화번호
    @PropertyElement
    val XPos: String?, // x 좌표
    @PropertyElement
    val YPos: String? // y 좌표
) {
    constructor() : this(null, null, null, null, null, null)
}
