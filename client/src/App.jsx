import { Route, Routes } from 'react-router-dom';
import { useCallback, useState } from 'react';
import Header from './components/Header/Header';
import Home from './pages/Home/Home';
import { apiFetch } from './api/api';
import styles from './App.module.scss';

function App() {
  const [regionQuery, setRegionQuery] = useState('');
  const [weatherResponse, setWeatherResponse] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  
  // useCallback : 함수를 메모이제이션하여, 의존성 배열에 포함된 상태 변수의 값(상태)이 변경되지 않는 한, 리렌더링 시에도 동일한 함수 참조를 유지함
  const handleSearch = useCallback(async (region) => {
    const trimmed = (region ?? '').trim(); // (region ?? '')  →  (region === null || region === undefined) ? '' : region
    setRegionQuery(trimmed);

    if(!trimmed) {
      setWeatherResponse(null);
      setErrorMessage('지역을 입력해 주세요.');
      return;
    }

    setIsLoading(true);
    setErrorMessage('');

    try {
      const res = await apiFetch(`/api/weather/now?region=${encodeURIComponent(trimmed)}`);
      setWeatherResponse(res);
    }catch(err) {
      setWeatherResponse(null);
      setErrorMessage(err.message);
    }finally {
      setIsLoading(false);
    }
  }, []);

  return (
    <>
      <Header onSearch={handleSearch} defaultValue={regionQuery} isLoading={isLoading} />
      <main className={styles.pageContainer}>
        <Routes>
          <Route
            path="/"
            element={
              <Home
                regionQuery={regionQuery}
                weatherResponse={weatherResponse}
                isLoading={isLoading}
                errorMessage={errorMessage}
              />
            }
          />
        </Routes>
      </main>
    </>
  );
}

export default App;