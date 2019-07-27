$(function () {
    //初始化
    initialCookie();
    initialData();

    /******
     * event
     ******/
    //点击面板main时
    $("#div_main").click(function () {
        var div = $("#div_peelPanel");
        if (div.css("display") === "block") {
            div.slideUp();
        }
    });
    //点击顶部换肤img时
    $("#div_peelPanel").find(">li>img").click(function () {
        var background = $("#div_background");
        var url = $(this).parent("li").attr("value");
        if(url !== null && url !== ""){
            if(url !== background.css("background-image")){
                background.css("background-image", url);
                cookieUtil.setCookie("backgroundImageUrl", url,365);
            }
        }
    });
    //点击顶部换肤标签时
    $("#txt_peel").click(function () {
        var div = $("#div_peelPanel");
        if(div.css("display")==="block"){
            div.slideUp();
        } else {
            div.slideDown();
        }
    });
    //点击表单登录按钮时
    $("#btn_login").click(function () {
        //表单验证
        var username = $.trim($("#input_username").val());
        var password = $.trim($("#input_password").val());
        if(username === "" || password === "") {
            styleUtil.errorShow($("#txt_error_msg"),"请输入用户名和密码");
            return;
        }
        $.ajax({
            url: "/tmall/admin/login/doLogin",
            type:"post",
            data: {"username":username,"password":password},
            success:function (data) {
                $("#btn_login").val("登录");
                if (data.success) {
                    cookieUtil.setCookie("username", username, 30);
                    location.href = "/tmall/admin";
                } else {
                    styleUtil.errorShow($("#txt_error_msg"), "用户名或密码错误");
                }
            },
            beforeSend:function () {
                $("#btn_login").val("登录中...");
            },
            error:function (data) {

            }
        });
    });
    //获得文本框焦点时
    $("#input_username,#input_password").focus(function () {
        //移除校验错误
        var msg = $("#txt_error_msg");
        styleUtil.errorHide(msg);
    });
    //失去用户名文本框焦点时
    $("#input_username").blur(function () {
        getUserProfilePicture($(this).val());
    });
});

//初始化Cookie数据
function initialCookie() {
    var url;
    var username;
    if(document.cookie.length>0) {
        username = cookieUtil.getCookie("username");
        url = cookieUtil.getCookie("backgroundImageUrl");
        if(url !== null) {
            $("#div_background").css("background-image", url);
        } else {
            $("#div_background").css("background-image", "url(/tmall/res/images/admin/loginPage/background-1.jpg)");
        }
        if(username !== null){
            $("#input_username").val(username);
            getUserProfilePicture(username);
        }
    } else {
        $("#div_background").css("background-image", "url(/tmall/res/images/admin/loginPage/background-1.jpg)");
    }
}
//初始化页面数据
function initialData() {
    //顶部时间
    $("#txt_date").text(new Date().toLocaleString());
    setInterval(function () {
        $("#txt_date").text(new Date().toLocaleString());
    }, 1000);
    //表单焦点
    var txt_username = $("#input_username");
    var username = $.trim(txt_username.val());
    if(username !== null && username !== ""){
        $("#input_password").focus();
        return;
    }
    txt_username.focus();
}
//获取用户头像
function getUserProfilePicture(username) {
    if(username !== null && username !== ""){
        $.getJSON("/tmall/admin/login/profile_picture",{"username":username},function (data) {
            if(data.success){
                if(data.srcString !== null){
                    $("#img_profile_picture").attr("src", "/tmall/res/images/item/adminProfilePicture/" + data.srcString);
                    return true;
                }
            }
        });
    }
    $("#img_profile_picture").attr("src","/tmall/res/images/admin/loginPage/default_profile_picture-128x128.png");
}