

const JobList = ({ route, startLocation }) => {
  return (
    <div className="job-list">
      <h2>Optimized Visit Order</h2>

      <div className="job-card start">
        <span className="order-badge">🏁 START</span>
        <div className="job-info">
          <strong>{startLocation}</strong>
          <p>Starting Point</p>
        </div>
      </div>

      {route.map((job) => (
        <div key={job.id} className="job-card">
          <span className="order-badge">#{job.visitOrder}</span>
          <div className="job-info">
            <strong>{job.store_name}</strong>
            <p>{job.job_id} — {job.job_type}</p>
            <p className="territory">📍 {job.territory}</p>
            <p className="distance">📏 {job.distanceFromPrevious} km from previous stop</p>
            <p className="time">⏱️ ~{job.estimatedTime} travel time</p>
          </div>
          <span className={`status ${job.status.toLowerCase()}`}>
            {job.status}
          </span>
        </div>
      ))}
    </div>
  );
};

export default JobList;