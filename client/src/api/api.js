// 현재 Vite 실행 mode에 따라 설정된 환경 변수 값을 읽어옴  →  "npm run dev" 실행 시 .env.development 파일에 정의된 환경 변수를 참조함
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const apiFetch = async (path, options = {}) => {
    const res = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        }
    });
    
    /*
        Q. 왜 굳이, 직접 상태 코드를 체크해서 throw해야 하는 거지?
           어차피 서버에서 에러가 발생하면, 자동으로 throw되지 않나?

        A. fetch는 네트워크 에러(서버 다운, CORS, 네트워크 단절 등)가 발생했을 때만 throw한다고 한다.
           즉, HTTP 에러(404, 500 등)는 정상 응답이라 판단하고 throw하지 않는다.
    */
    if(!res.ok) throw new Error('조회 실패');
    return res.json();
};