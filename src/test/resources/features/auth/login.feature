@student @auth @login
Feature: Đăng nhập vào Cổng thông tin đào tạo

  Là một sinh viên
  Tôi muốn đăng nhập vào Cổng thông tin đào tạo
  Để có thể truy cập các chức năng dành cho sinh viên

  @smoke @TC_LOGIN_001
  Scenario: Đăng nhập thành công với thông tin hợp lệ
    Given người dùng đang ở trang đăng nhập
    When người dùng đăng nhập với thông tin hợp lệ
    Then người dùng được chuyển đến trang thông tin sinh viên

  @negative @TC_LOGIN_002
  Scenario: Đăng nhập thất bại với tên đăng nhập không hợp lệ
    Given người dùng đang ở trang đăng nhập
    When người dùng nhập tên đăng nhập không hợp lệ
    And người dùng nhập mật khẩu
    And người dùng nhấn nút đăng nhập
    Then hệ thống hiển thị thông báo "Tên đăng nhập hoặc mật khẩu không chính xác"
    And người dùng vẫn ở trang đăng nhập

  @negative @TC_LOGIN_003
  Scenario: Đăng nhập thất bại với mật khẩu không hợp lệ
    Given người dùng đang ở trang đăng nhập
    When người dùng nhập tên đăng nhập hợp lệ
    And người dùng nhập mật khẩu không hợp lệ
    And người dùng nhấn nút đăng nhập
    Then hệ thống hiển thị thông báo "Tên đăng nhập hoặc mật khẩu không chính xác"
    And người dùng vẫn ở trang đăng nhập

  @negative @TC_LOGIN_004
  Scenario: Không nhập tên đăng nhập
    Given người dùng đang ở trang đăng nhập
    When người dùng chỉ nhập mật khẩu
    And người dùng nhấn nút đăng nhập
    Then hệ thống hiển thị lỗi "Tên đăng nhập là bắt buộc"

  @negative @TC_LOGIN_005
  Scenario: Không nhập mật khẩu
    Given người dùng đang ở trang đăng nhập
    When người dùng chỉ nhập tên đăng nhập
    And người dùng nhấn nút đăng nhập
    Then hệ thống hiển thị lỗi "Mật khẩu là bắt buộc"

  @negative @TC_LOGIN_006
  Scenario: Không nhập tên đăng nhập và mật khẩu
    Given người dùng đang ở trang đăng nhập
    When người dùng nhấn nút đăng nhập
    Then hệ thống hiển thị lỗi "Tên đăng nhập là bắt buộc"