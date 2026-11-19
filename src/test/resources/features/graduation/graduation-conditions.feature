
@student @graduation
Feature: Kiểm thử điều kiện tốt nghiệp
  Là sinh viên đã đăng nhập
  Tôi muốn xem các điều kiện tốt nghiệp
  Để theo dõi thông tin và kết quả hiển thị của chương trình đào tạo

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_18 @ui
  Scenario: Hiển thị hộp thoại điều kiện tốt nghiệp
    When sinh viên mở điều kiện tốt nghiệp
    Then hộp thoại điều kiện tốt nghiệp hiển thị đúng chương trình
    And bảng điều kiện tốt nghiệp có đúng các cột

  @TC_18 @data-verification
  Scenario: Đối chiếu các điều kiện tốt nghiệp với baseline
    When sinh viên mở điều kiện tốt nghiệp
    Then dữ liệu điều kiện tốt nghiệp khớp baseline TC_18
