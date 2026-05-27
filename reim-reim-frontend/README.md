# chailv-project

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Customize configuration

See [Vite Configuration Reference](https://vitejs.dev/config/).

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Compile and Minify for Production

```sh
npm run build
```

### Lint with [ESLint](https://eslint.org/)

```sh
npm run lint
```
1. API层重构
    - itinerary.js: 路径改为 /api/reimbursement/forms/{formUid}/itineraries
    - subsidy.js: 路径改为 /api/reimbursement/subsidies/{id}/calendar
    - reimForm.js: 新增 withdrawReimForm 撤回接口

2. FormScript.js 修复
    - saveTrip: 补充 travelerId 字段，日期格式化为 yyyy-MM-dd
    - copyTrip/deleteTrip: 适配新的API签名 (formUid, itineraryUid)
    - editSubsidy: 改为从后端加载补助日历，不再本地硬编码计算
    - saveHelp: 改为调用后端API保存，保存后自动刷新详情
    - saveTrip: 保存后自动刷新行程+补助列表
    - cityOptions: 改为从API加载，移除硬编码数据
    - setTrip: 增加报销人必选校验
    - 补助日历默认值: 实际金额fallback到标准金额，标准金额fallback到实际金额

3. MainView.vue 修复
    - 撤回按钮对接后端 /withdraw 接口
