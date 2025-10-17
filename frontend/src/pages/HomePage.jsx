import { useState } from 'react';
import { ScheduleSearchForm } from 'components/features/schedules/ScheduleSearchForm';
import { ScheduleResultsList } from 'components/features/schedules/ScheduleResultsList';
import { getSchedules } from 'api/schedules';

export function HomePage() {
  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [lastQuery, setLastQuery] = useState(null);

  const search = async ({ from, to, date }) => {
    setLoading(true);
    setError(null);
    const q = { from, to, date };
    setLastQuery(q);
    try {
      const data = await getSchedules(q);
      setSchedules(data);
    } catch (e) {
      setError(e.message || 'Failed to load schedules');
      setSchedules([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <ScheduleSearchForm onSearch={search} />
      <ScheduleResultsList
        schedules={schedules}
        loading={loading}
        error={error}
        onRetry={() => lastQuery && search(lastQuery)}
      />
    </div>
  );
}