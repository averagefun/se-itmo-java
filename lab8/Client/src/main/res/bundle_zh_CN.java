import java.util.ListResourceBundle;

public class bundle_zh_CN extends ListResourceBundle {
    public Object[][] getContents() {
        return contents;
    }

    private Object[][] contents = {
            {"titleAuth", "授权"},
            {"login", "用户名"},
            {"password", "密码"},
            {"signUp", "注册帐号"},
            {"logIn", "登入"},
            {"logTooShort", "用户名必须至少三个字符"},
            {"passTooShort", "密码必须至少三个字符"},
            {"userAlreadyExists", "用户已存在"},
            {"wrongEmail", "错误的电邮地址"},
            {"userDoesntExist", "用户不存在"},
            {"waiting", "少候"},
            {"wrongPassword", "错误的密码"},
            {"token", "数字代币: "},
            {"deadToken", "你的时间结束了"},
            {"wrongToken", "错误的数字代币"},
            {"signUpError", "注册帐号失败"},
            {"SQLException", "数据库不可用"},
            {"incorrectCommand", "命令不正确"},
            {"send", "寄去"},
            {"title", "图形用户界面"},
            {"language", "语"},
            {"exit", "退出"},
            {"info", "生物信息"},
            {"name", "名字"},
            {"family", "属"},
            {"hunger", "饥饿"},
            {"location", "座落"},
            {"creationTime", "创作时间"},
            {"time", "时间"},
            {"inventory", "东西"},
            {"user", "用户"},
            {"format", "格式"},
            {"size", "大小"},
            {"change", "改变"},
            {"cancel", "关闭"},
            {"refresh", "复新"},
            {"disconnected", "服务器不可用"},
            {"connected", "建立连接"},
            {"AddedSuccess", "成功添加了"},
            {"RemovedSuccess", "成功删除了"},
            {"SavedSuccess", "成功保存了"},
            {"AddedFailing", "未能添"},
            {"RemovedFailingDontYours", "未能删除:这不是你的生物!"},
            {"SavedFailing", "未能保存"},
            {"loadFileError", "未能下载文件"},
            {"JSONError", "无效的JSON格式"},
            {"greeting", "我知道你去那儿 ..."},
            {"newCreature", "新的"},
            {"creatures", "生物"},
            {"add", "建"},
            {"add_if_max", "建(最大尺寸)"},
            {"remove", "删除"},
            {"isEmpty", "没有填补!"},
            {"isnNumber", "不是数字!"},
            {"isnPositive", "必须大于零!"},
            {"isnChosen", "没有选中了"},
            {"added", "建了"},
            {"tryAgain", "服务器不可用,再试一次"},
            {"incorrectDate", "日期格式"},
            {"ChangedSuccess", "成功改变了"},
            {"ChangedFailing", "未能改变"},
            {"ChangedFailingDontYours", "未能改变:这不是你的生物!"},
            {"windowWillBeClosed", "你的时间结束了。该窗口将被关闭!"},
            {"incorrectX", "非法X!"},
            {"incorrectY", "非法Y!"},
            {"incorrectSize", "非法大小!"},
            {"incorrectHunger", "非法饥饿程度!!"},
            {"incorrectDate", "非法日期格式!"},
            {"NoCreaturesFound", "没找到生物"},
            {"deleted", "删除了"},
            {"kek", "连接丢失了<br>哈哈"},
            {"color", "颜色"},
            {"clear", "清除"},
            {"CreaturesDoesntChanged", "生物没有改变"},
            {"ChangedFailedCreatureExists", "更改失败：生物存在"},
            {"AddedFailedCreatureExists", "添加失败：生物存在"},
    };
}
