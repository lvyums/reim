package com.viessmart.reimburse.tools;

public class FormIdContext {

    private static final ThreadLocal<Form> FORM_THREAD_LOCAL = new ThreadLocal<>();

    // 设置表单信息（正确写法）
    public static void setForm(Long formUid, String reimburserId) {
        Form form = new Form();
        form.setFormUid(formUid);
        form.setReimburserId(reimburserId);
        FORM_THREAD_LOCAL.set(form);
    }

    // 获取整个表单对象
    public static Form getForm() {
        return FORM_THREAD_LOCAL.get();
    }

    // 快捷获取 formUid
    public static Long getFormUid() {
        Form form = FORM_THREAD_LOCAL.get();
        return form == null ? null : form.getFormUid();
    }

    // 快捷获取 reimburserId
    public static String getReimburserId() {
        Form form = FORM_THREAD_LOCAL.get();
        return form == null ? null : form.getReimburserId();
    }

    // 清空（必须）
    public static void remove() {
        FORM_THREAD_LOCAL.remove();
    }
}