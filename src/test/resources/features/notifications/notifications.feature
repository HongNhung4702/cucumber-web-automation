
@student @notifications
Feature: Tin nhắn và thông báo cá nhân
  Là sinh viên đã đăng nhập vào Cổng thông tin đào tạo
  Tôi muốn xem và tìm kiếm thông báo cá nhân
  Để theo dõi các thông tin liên quan đến tài khoản của mình

  Background:
    Given sinh viên đã đăng nhập thành công vào hệ thống

  @TC_10 @smoke
  Scenario: Xem danh sách thông báo cá nhân
    Given tài khoản sinh viên có thông báo hóa đơn điện tử
    When sinh viên truy cập mục Tin nhắn
    And sinh viên chọn tab Thông báo cá nhân
    Then hệ thống hiển thị danh sách thông báo cá nhân
    And bảng thông báo có các cột "Tiêu đề", "Người gửi", "Thời gian gửi"
    When sinh viên mở một thông báo hóa đơn điện tử trong danh sách
    And sinh viên truy cập liên kết tải hóa đơn PDF của thông báo
    Then hóa đơn PDF được tải thành công
    And họ tên và mã sinh viên trên hóa đơn khớp với tài khoản đang đăng nhập
    And thông báo hóa đơn được kiểm tra có nội dung thuộc đúng sinh viên

  @TC_11 @search
  Scenario: Tìm kiếm thông báo bằng từ khóa có trong dữ liệu
    Given sinh viên đang ở tab Thông báo cá nhân
    And tài khoản sinh viên có thông báo chứa từ khóa "08"
    When sinh viên nhập từ khóa "08" vào ô tìm kiếm thông báo
    Then hệ thống hiển thị danh sách thông báo phù hợp với từ khóa "08"
    And các thông báo trong kết quả chứa từ khóa "08" theo phạm vi tìm kiếm của hệ thống
    And kết quả tìm kiếm chỉ bao gồm các thông báo thuộc tài khoản đang đăng nhập

  @TC_12 @search @negative
  Scenario: Tìm kiếm thông báo bằng từ khóa không có trong dữ liệu
    Given sinh viên đang ở tab Thông báo cá nhân
    And tài khoản sinh viên không có thông báo phù hợp với từ khóa "Giấy phép"
    When sinh viên nhập từ khóa "Giấy phép" vào ô tìm kiếm thông báo
    Then hệ thống hiển thị trạng thái không có thông báo phù hợp
    And không có bản ghi thông báo nào được hiển thị trong kết quả tìm kiếm
    And không phát sinh lỗi khi tìm kiếm thông báo

  @TC_13
  Scenario: Xem thông báo từ biểu tượng chuông
    Given tài khoản sinh viên có thông báo cá nhân
    When sinh viên nhấn biểu tượng chuông thông báo
    Then bảng thông báo cá nhân được hiển thị
    And bảng thông báo hiển thị các thông báo thuộc tài khoản đang đăng nhập
    And thông tin các thông báo trong bảng khớp với thông báo cá nhân tương ứng của tài khoản
    And bảng thông báo có liên kết "Xem thêm"

  @TC_14
  Scenario: Xem thêm thông báo từ biểu tượng chuông
    Given sinh viên đã mở bảng thông báo từ biểu tượng chuông
    When sinh viên nhấn liên kết "Xem thêm"
    Then hệ thống điều hướng đến khu vực Thông báo cá nhân
    And khu vực Thông báo cá nhân được hiển thị
