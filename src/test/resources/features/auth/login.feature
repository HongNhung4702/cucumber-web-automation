
@student @auth @login
Feature: Kiểm thử chức năng đăng nhập và bảo vệ phiên làm việc

  Là một sinh viên
  Tôi muốn đăng nhập và sử dụng hệ thống an toàn
  Để truy cập thông tin cá nhân và bảo vệ dữ liệu

  @smoke @TC_01
  Scenario: Đăng nhập thành công với tài khoản hợp lệ
    Given người dùng đang ở trang đăng nhập
    When người dùng thực hiện đăng nhập
    Then người dùng được chuyển đến trang thông tin sinh viên

  @negative
  Scenario Outline: Đăng nhập với thông tin không hợp lệ - <truong_hop>
    Given người dùng đang ở trang đăng nhập
    When người dùng thực hiện đăng nhập
    Then hệ thống hiển thị thông báo đăng nhập không hợp lệ
    And người dùng vẫn ở trang đăng nhập

    @TC_02
    Examples: Mật khẩu không hợp lệ
      | truong_hop           |
      | Mật khẩu không hợp lệ |

    @TC_03
    Examples: Tên đăng nhập không hợp lệ
      | truong_hop                |
      | Tên đăng nhập không hợp lệ |

  @security @TC_04
  Scenario: Kiểm tra mật khẩu được che khi nhập
    Given người dùng đang ở trang đăng nhập
    When người dùng nhập mật khẩu vào ô mật khẩu
    Then nội dung mật khẩu phải được che

  @security @TC_06
  Scenario: Kiểm tra vô hiệu hóa phiên giữa hai tab
    Given sinh viên đã đăng nhập thành công
    When sinh viên mở trang Tiến độ học tập ở tab thứ nhất
    And sinh viên mở trang Xem điểm ở tab thứ hai
    And sinh viên đăng xuất tại tab thứ nhất
    And sinh viên chuyển sang tab thứ hai và tải lại trang
    Then tab thứ hai không còn hiển thị dữ liệu điểm
    And hệ thống yêu cầu đăng nhập lại
