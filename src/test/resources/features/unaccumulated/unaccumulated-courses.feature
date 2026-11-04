
@student @unaccumulated-courses
Feature: Xem danh sách học phần chưa tích lũy
  Là sinh viên đã đăng nhập hệ thống
  Tôi muốn xem danh sách học phần chưa tích lũy theo chương trình đào tạo
  Để theo dõi các học phần chưa được tích lũy trong chương trình

  @TC_17 @ui
  Scenario: Hiển thị danh sách học phần chưa tích lũy đúng tài khoản và chương trình
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ
    When sinh viên mở trang Tổng quan học tập
    And sinh viên chọn chương trình đào tạo "Kỹ thuật phần mềm" cho TC_17
    And sinh viên mở danh sách học phần chưa tích lũy
    Then thông tin sinh viên và chương trình đào tạo khớp ngữ cảnh TC_17
    And hộp thoại Học phần chưa tích lũy hiển thị đúng tiêu đề
    And bảng học phần chưa tích lũy hiển thị đầy đủ các cột đã xác nhận

  @TC_17 @data-verification
  Scenario: Đối chiếu danh sách học phần chưa tích lũy với baseline
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ
    When sinh viên mở trang Tổng quan học tập
    And sinh viên chọn chương trình đào tạo "Kỹ thuật phần mềm" cho TC_17
    And sinh viên mở danh sách học phần chưa tích lũy
    Then số lượng học phần chưa tích lũy khớp baseline của TC_17
    And tổng số tín chỉ trong danh sách khớp baseline của TC_17
    And toàn bộ dữ liệu học phần chưa tích lũy khớp baseline của TC_17
