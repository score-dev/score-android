package com.team.score.API.neis.response

data class NeisSchoolResponse (
    val schoolInfo: List<SchoolInfo>?
)

data class SchoolInfo(
    val head: List<Head>?,
    val row: List<School>?
)

data class Head(
    val list_total_count: Int
)

data class School(
    val ATPT_OFCDC_SC_NM: String, // 교육청명
    val SD_SCHUL_CODE: String,    // 학교 코드
    val SCHUL_NM: String,         // 학교명
    val ENG_SCHUL_NM: String?,    // 영문 학교명
    val SCHUL_KND_SC_NM: String,  // 학교종류명 (초등, 중등, 고등)
    val LCTN_SC_NM: String,       // 지역명
    val FOND_SC_NM: String,       // 설립구분 (공립, 사립)
    val ORG_RDNMA: String,        // 주소
    val ORG_TELNO: String?        // 전화번호
)

data class SchoolDto(
    val SD_SCHUL_CODE: String,    // 학교 코드
    val SCHUL_NM: String,         // 학교명
    val ORG_RDNMA: String         // 주소
)