FILETRANS_LANG = {
  LANG0: { code: "dxksRAMqw2qWxxOG", desc: "普通话" },
  LANG1: { code: "kH1y8ns3kk8lXxr0", desc: "英语" },
  LANG2: { code: "J8hJxw8XQWUzRVkA", desc: "中英混合" },
  LANG3: { code: "yaXsjWbzmAaiHiGD", desc: "日语" },
  LANG4: { code: "e5us7Q2KNRkJihfK", desc: "泰语" },
  LANG5: { code: "fguvKlRZkpVXZY3W", desc: "韩语" },
  LANG6: { code: "v6gsHNemSH95SGGX", desc: "粤语" },
  LANG7: { code: "U8r9efQeREfJkg15", desc: "四川话" },
  LANG8: { code: "zTw21e2aAyJzxrSp", desc: "河南话" },
};

// ========== 实时语音识别语言枚举 ==========
// 识别语言（对应阿里云 NLS AppKey）
ASR_LANG = {
  LANG0: { appKey: "dxksRAMqw2qWxxOG", nlsLanguage: "zh-CN", desc: "中文（普通话）" },
  LANG1: { appKey: "kH1y8ns3kk8lXxr0", nlsLanguage: "en-US", desc: "英语" },
  LANG2: { appKey: "J8hJxw8XQWUzRVkA", nlsLanguage: "zh-CN", desc: "中英混合" },
  LANG3: { appKey: "yaXsjWbzmAaiHiGD", nlsLanguage: "ja-JP", desc: "日语" },
  LANG4: { appKey: "e5us7Q2KNRkJihfK", nlsLanguage: "th-TH", desc: "泰语" },
  LANG5: { appKey: "fguvKlRZkpVXZY3W", nlsLanguage: "ko-KR", desc: "韩语" },
  LANG6: { appKey: "v6gsHNemSH95SGGX", nlsLanguage: "zh-CN", desc: "粤语" },
  LANG7: { appKey: "U8r9efQeREfJkg15", nlsLanguage: "zh-CN", desc: "四川话" },
  LANG8: { appKey: "zTw21e2aAyJzxrSp", nlsLanguage: "zh-CN", desc: "河南话" },
};

// 翻译语言（对应百度翻译 API 语言代码）
TRANSLATE_LANG = {
  LANG0: { code: "zh", desc: "中文" },
  LANG1: { code: "en", desc: "英语" },
  LANG2: { code: "zh", desc: "中英混合" },
  LANG3: { code: "jp", desc: "日语" },
  LANG4: { code: "th", desc: "泰语" },
  LANG5: { code: "kor", desc: "韩语" },
  LANG6: { code: "zh", desc: "粤语" },
  LANG7: { code: "zh", desc: "四川话" },
  LANG8: { code: "zh", desc: "河南话" },
};

// 前端 LANG 类型映射到后端实际语言代码（用于 WebSocket start 消息）
ASR_LANG_TO_NLS_LANGUAGE = {
  "LANG0": "zh-CN",
  "LANG1": "en-US",
  "LANG2": "zh-CN",
  "LANG3": "ja-JP",
  "LANG4": "th-TH",
  "LANG5": "ko-KR",
  "LANG6": "zh-CN",
  "LANG7": "zh-CN",
  "LANG8": "zh-CN",
};

// 前端 LANG 类型映射到翻译语言代码（用于百度翻译 API）
ASR_LANG_TO_TRANSLATE_CODE = {
  "LANG0": "zh",
  "LANG1": "en",
  "LANG2": "zh",
  "LANG3": "jp",
  "LANG4": "th",
  "LANG5": "kor",
  "LANG6": "zh",
  "LANG7": "zh",
  "LANG8": "zh",
};

// 识别语言数组（用于前端下拉选项）
ASR_LANG_ARRAY = [
  { value: "LANG0", label: "中文（普通话）" },
  { value: "LANG1", label: "英语" },
  { value: "LANG2", label: "中英混合" },
  { value: "LANG3", label: "日语" },
  { value: "LANG4", label: "泰语" },
  { value: "LANG5", label: "韩语" },
  { value: "LANG6", label: "粤语" },
  { value: "LANG7", label: "四川话" },
  { value: "LANG8", label: "河南话" },
];

// 翻译语言数组（用于前端下拉选项）
TRANSLATE_LANG_ARRAY = [
  { value: "LANG0", label: "中文" },
  { value: "LANG1", label: "英语" },
  { value: "LANG3", label: "日语" },
  { value: "LANG4", label: "泰语" },
  { value: "LANG5", label: "韩语" },
];

// UI 显示用的语言 key 映射
UI_LANG_KEY_MAP = {
  "LANG0": "zh",
  "LANG1": "en",
  "LANG2": "zh",
  "LANG3": "ja",
  "LANG4": "th",
  "LANG5": "ko",
  "LANG6": "zh",
  "LANG7": "zh",
  "LANG8": "zh",
};

// UI 文案
UI_LANG_TEXT = {
  liveSubtitlePlaceholder: {
    zh: "等待语音输入...",
    en: "Waiting for speech input...",
    ja: "音声入力を待っています...",
    th: "กำลังรอเสียงพูด...",
    ko: "음성 입력을 기다리는 중...",
  },
  translationPlaceholder: {
    zh: "等待翻译结果...",
    en: "Waiting for translation...",
    ja: "翻訳結果を待っています...",
    th: "กำลังรอผลแปล...",
    ko: "번역 결과를 기다리는 중...",
  },
  translationDisabledText: {
    zh: "当前为仅提取字幕模式，不显示翻译",
    en: "Subtitle-only mode: translation is hidden",
    ja: "字幕のみモード：翻訳は表示されません",
    th: "โหมดคำบรรยายเท่านั้น: การแปลจะไม่แสดง",
    ko: "자막 전용 모드: 번역이 표시되지 않음",
  },
};
FILETRANS_STATUS = {
  INIT: { code: "I", desc: "初始" },
  SUBTITLE_INIT: { code: "SI", desc: "待生成字幕" },
  SUBTITLE_PENDING: { code: "SP", desc: "生成字幕中" },
  SUBTITLE_SUCCESS: { code: "SS", desc: "生成字幕成功" },
  SUBTITLE_FAILURE: { code: "SF", desc: "生成字幕失败" },
  DELETED: { code: "D", desc: "失效" },
};
FILETRANS_PAY_STATUS = {
  I: { code: "I", desc: "未支付" },
  P: { code: "P", desc: "处理中" },
  S: { code: "S", desc: "支付成功" },
  F: { code: "F", desc: "支付失败" },
};

FILETRANS_LANG_ARRAY = [
  { code: "dxksRAMqw2qWxxOG", desc: "普通话" },
  { code: "kH1y8ns3kk8lXxr0", desc: "英语" },
  { code: "J8hJxw8XQWUzRVkA", desc: "中英混合" },
  { code: "yaXsjWbzmAaiHiGD", desc: "日语" },
  { code: "e5us7Q2KNRkJihfK", desc: "泰语" },
  { code: "fguvKlRZkpVXZY3W", desc: "韩语" },
  { code: "v6gsHNemSH95SGGX", desc: "粤语" },
  { code: "U8r9efQeREfJkg15", desc: "四川话" },
  { code: "zTw21e2aAyJzxrSp", desc: "河南话" },
];
FILETRANS_STATUS_ARRAY = [
  { code: "I", desc: "初始" },
  { code: "SI", desc: "待生成字幕" },
  { code: "SP", desc: "生成字幕中" },
  { code: "SS", desc: "生成字幕成功" },
  { code: "SF", desc: "生成字幕失败" },
  { code: "D", desc: "失效" },
];
FILETRANS_PAY_STATUS_ARRAY = [
  { code: "I", desc: "未支付" },
  { code: "P", desc: "处理中" },
  { code: "S", desc: "支付成功" },
  { code: "F", desc: "支付失败" },
];
