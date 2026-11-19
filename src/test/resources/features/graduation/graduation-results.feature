
@student @graduation
Feature: Kiểm thử kết quả xét tốt nghiệp
  Là sinh viên đã đăng nhập
  Tôi muốn xem kết quả xét tốt nghiệp
  Để kiểm tra các kết quả và ghi chú được hệ thống hiển thị

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_19 @ui
  Scenario: Hiển thị hộp thoại kết quả xét tốt nghiệp
    When sinh viên mở kết quả xét tốt nghiệp
    Then hộp thoại kết quả xét tốt nghiệp hiển thị đúng chương trình
    And bảng kết quả xét tốt nghiệp có đúng các cột

  @TC_19 @data-verification
  Scenario: Đối chiếu kết quả xét tốt nghiệp với baseline
    When sinh viên mở kết quả xét tốt nghiệp
    Then dữ liệu kết quả xét tốt nghiệp khớp baseline TC_19
