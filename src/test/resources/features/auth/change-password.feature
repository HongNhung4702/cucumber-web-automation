
@student @auth @change-password
Feature: Đổi mật khẩu tài khoản sinh viên
  Là một sinh viên đã đăng nhập
  Tôi muốn thay đổi mật khẩu tài khoản
  Để quản lý thông tin xác thực của mình

  Background:
    Given sinh viên đã đăng nhập vào Cổng thông tin đào tạo

  @TC_07 @smoke
  Scenario: Truy cập chức năng Đổi mật khẩu
    When sinh viên nhấn biểu tượng tài khoản
    And sinh viên chọn chức năng Đổi mật khẩu
    Then hộp thoại Đổi mật khẩu được hiển thị
    And hộp thoại có trường Mật khẩu cũ
    And hộp thoại có trường Mật khẩu mới
    And hộp thoại có trường Nhập lại mật khẩu mới
    And nút Đổi mật khẩu được hiển thị

  @TC_08 @account-modification
  Scenario: Đổi mật khẩu thành công với thông tin hợp lệ
    Given sinh viên đã mở hộp thoại Đổi mật khẩu
    And tài khoản kiểm thử được phép thay đổi mật khẩu
    When sinh viên nhập mật khẩu cũ chính xác
    And sinh viên nhập mật khẩu mới hợp lệ
    And sinh viên nhập lại mật khẩu mới trùng khớp
    And sinh viên nhấn nút Đổi mật khẩu
    Then hệ thống thông báo đổi mật khẩu thành công
    And mật khẩu mới được áp dụng cho tài khoản sinh viên

  @TC_09 @negative
  Scenario: Đổi mật khẩu với mật khẩu cũ không chính xác
    Given sinh viên đã mở hộp thoại Đổi mật khẩu
    When sinh viên nhập mật khẩu cũ không chính xác
    And sinh viên nhập mật khẩu mới hợp lệ
    And sinh viên nhập lại mật khẩu mới trùng khớp
    And sinh viên nhấn nút Đổi mật khẩu
    Then hệ thống hiển thị thông báo "Mật khẩu cũ không chính xác"
    And hộp thoại Đổi mật khẩu vẫn được hiển thị
    And mật khẩu của tài khoản sinh viên không bị thay đổi
