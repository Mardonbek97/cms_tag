<%@ page contentType="text/html;charset=WINDOWS-1251" language="java" %>
<%@ page import="java.sql.*,java.util.*, uz.fido_biznes.cms.*" %>
<%@ page import="uz.fido_biznes.cms.Util" %>
<%@ taglib uri="/WEB-INF/cms.tld" prefix="t" %>
<%
	//-------------------------------------------------------------------------------------------------
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy");
	String year_now = sdf.format(new java.util.Date());
	String v = "?v=" + new java.util.Date().getTime();
%><t:page>
	<!DOCTYPE HTML>
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=WINDOWS-1251">
		<link rel="shortcut icon" href="user/util/login/icon/iabs.ico" />
		<meta http-equiv="Content-Security-Policy"
		      content="default-src 'self' 'unsafe-inline' 'unsafe-eval'; connect-src 'self'  ws://localhost:8088/app data: blob:">
		<title>CROBS</title>
		<!--link rel="stylesheet" href="user/font/inter/inter.css" /-->
		<link rel="stylesheet" type="text/css" href="user/util/login/css/index.css" />
		<script src="user/util/login/js/tripledes.js<%=v%>"></script>
		<script src="user/util/login/js/sha1.js<%=v%>"></script>
		<script src="user/config/config.js<%=v%>"></script>
		<script src="user/soft/iabs-client.js<%=v%>"></script>
		<script src="user/js/hashtable.js<%=v%>"></script>
		<script src="user/util/login/js/index.js<%=v%>"></script>
		<style>
			* {
				box-sizing: border-box;
				font-family: 'Inter', sans-serif;
				margin: 0;
				padding: 0;
			}

			body {
				display: flex;
				height: 100vh;
				background: linear-gradient(120deg, #f7f8fa 0%, #e5ecf6 50%, #d9e2f0 100%);
			}

			/* Chap blok */
			.login-section {
				width: 50%;
				display: flex;
				flex-direction: column;
				justify-content: center;
				align-items: center;
				text-align: center;
				padding: 0 60px;
			}

			.login-section img.logo {
				width: 80px;
				margin-bottom: 40px;
			}

			.login-section h1 {
				font-size: 22px;
				font-weight: 700;
				color: #1f2937;
				margin-bottom: 10px;
			}

			.login-section p {
				color: #6b7280;
				font-size: 14px;
				margin-bottom: 30px;
			}

			.login-section .form-group {
				position: relative;
				width: 100%;
				max-width: 320px;
				margin-bottom: 15px;
			}

			.form-group input {
				width: 100%;
				padding: 12px 40px 12px 40px;
				border: 1px solid #cbd5e1;
				border-radius: 6px;
				font-size: 14px;
				color: #374151;
				outline: none;
				background-color: #f9fafb;
			}

			.form-group input:focus {
				border-color: #5570f1;
				background-color: #fff;
			}

			.form-group i {
				position: absolute;
				top: 50%;
				left: 12px;
				transform: translateY(-50%);
				color: #9ca3af;
				font-size: 14px;
			}

			.login-section button {
				width: 100%;
				max-width: 320px;
				padding: 12px;
				border: none;
				border-radius: 6px;
				background-color: #334155;
				color: white;
				font-weight: 600;
				cursor: pointer;
				transition: background 0.2s ease;
			}

			.login-section button:hover {
				background-color: #1e293b;
			}

			.footer {
				position: absolute;
				bottom: 10px;
				left: 20px;
				font-size: 12px;
				color: #9ca3af;
			}

			.footer a {
				color: #64748b;
				text-decoration: none;
				margin-left: 10px;
			}

			/* O‘ng blok */
			.hero-section {
				width: 50%;
				background: url('user/bg_image.jpg') center center / cover no-repeat;
				border-top-left-radius: 20px;
				border-bottom-left-radius: 20px;
				display: flex;
				flex-direction: column;
				justify-content: flex-end;
				color: white;
				padding: 40px;
				position: relative;
			}

			.overlay {
				position: absolute;
				inset: 0;
				background: rgba(0, 0, 0, 0.3);
				border-top-left-radius: 20px;
				border-bottom-left-radius: 20px;
			}

			.hero-content {
				position: relative;
				z-index: 2;
			}

			.hero-content h2 {
				font-size: 20px;
				font-weight: 600;
				margin-bottom: 10px;
			}

			.hero-content p {
				font-size: 14px;
				line-height: 1.5;
				color: #e5e7eb;
				max-width: 400px;
				margin-bottom: 20px;
			}

			.hero-icons {
				display: flex;
				gap: 20px;
				font-size: 13px;
			}

			.hero-icons span {
				display: flex;
				align-items: center;
				gap: 6px;
				color: #d1d5db;
			}
		</style>
	</head>
	<body>
	<div class="login-form-lang">
		<div class="login-lang-dropdown" id="login-lang-dropdown">
			<button id="dropdown-btn" lang-value="RU">
				<img src="/ibs/user/util/login/img/ru.png" alt=""> Русский язык
				<span class="arrow-down"></span>
			</button>
			<ul class="dropdown-content" id="dropdown-content">
				<li value="RU" class="active">
					<img src="/ibs/user/util/login/img/ru.png" alt=""> Русский язык
				</li>
				<li value="UZC">
					<img src="/ibs/user/util/login/img/uz.png" alt=""> Ўзбек тили
				</li>
				<li value="UZL">
					<img src="/ibs/user/util/login/img/uz.png" alt=""> O'zbek tili
				</li>
				<li value="EN">
					<img src="/ibs/user/util/login/img/en.png" alt=""> English
				</li>
			</ul>
		</div>
	</div>
	<div class="login-section">
		<img src="https://upload.wikimedia.org/wikipedia/commons/a/a7/React-icon.svg" class="logo" alt="CROBS Logo">
		<h1>Добро пожаловать в CROBS!</h1>
		<p>Пожалуйста, введите свои данные для входа в<br>свою учетную запись.</p>
		<form name="fm" onsubmit="checkSubmit();" method="post">
			<div class="form-group">
				<i></i>
				<input type="text" name="u" id="lHolder" placeholder="Логин" value="1">
			</div>
			<div class="form-group">
				<i></i>
				<input type="password" name="p" id="pHolder" placeholder="Пароль" value="1">
			</div>
			<button name="s" onclick="checkSubmit()">Войти</button>
		</form>
		<div class="footer">
			© 2025 SQB <a href="#">Privacy Policy</a>
		</div>
	</div>
	<div class="hero-section">
		<div class="overlay"></div>
		<div class="hero-content">
			<h2>Добро пожаловать в корпоративный портал</h2>
			<p>Наш сервис объединяет инструменты для управления процессами, данными и отчетностью банка, помогая автоматизировать каждую сферу.</p>
		</div>
		<div id="iabs-client" style="display: none"></div>
	</div>
	</body>
	</body>
	</html>
</t:page>
