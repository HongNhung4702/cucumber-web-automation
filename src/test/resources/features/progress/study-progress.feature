
@student @study-progress
Feature: Kiểm thử tiến độ học tập
  Là sinh viên đã đăng nhập
  Tôi muốn kiểm tra tiến độ tích lũy tín chỉ
  Để theo dõi kết quả học tập so với kế hoạch đào tạo

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_20 @ui
  Scenario: Hiển thị hộp thoại kiểm tra tiến độ học tập
    When sinh viên mở kiểm tra tiến độ học tập
    Then hộp thoại tiến độ học tập hiển thị đúng chương trình
    And bảng tiến độ học tập có đúng các cột

  @TC_20 @data-verification
  Scenario: Đối chiếu tiến độ học tập với baseline
    When sinh viên mở kiểm tra tiến độ học tập
    Then dữ liệu tiến độ học tập khớp baseline TC_20
