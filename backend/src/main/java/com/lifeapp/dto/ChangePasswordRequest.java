package com.lifeapp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank(message = "旧密码不能为空")
    @Size(max = 128, message = "旧密码长度不能超过128位")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(max = 128, message = "新密码长度不能超过128位")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    @Size(max = 128, message = "确认密码长度不能超过128位")
    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
